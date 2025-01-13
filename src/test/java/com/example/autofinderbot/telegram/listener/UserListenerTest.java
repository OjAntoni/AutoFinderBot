package com.example.autofinderbot.telegram.listener;

import com.example.autofinderbot.configuration.BaseTelegramListenerTest;
import com.example.autofinderbot.domain.*;
import com.example.autofinderbot.repository.UserFilterRepository;
import com.example.autofinderbot.repository.UserRepository;
import com.example.autofinderbot.service.UserService;
import com.example.autofinderbot.telegram.exception.InvalidSearchUrlException;
import com.example.autofinderbot.telegram.exception.UserIsNotRedirectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;


import static com.example.autofinderbot.telegram.CommandPath.CONFIRM_FILTER;
import static com.example.autofinderbot.telegram.CommandPath.UPLOAD_URL;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserListenerTest extends BaseTelegramListenerTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserFilterRepository userFilterRepository;
    @SpyBean
    UserService userService;
    @Autowired
    UserListener userListener;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startBotWithRegisteredUser_PosTC() throws TelegramApiException {
        Mockito.when(update.getMessage().getChatId()).thenReturn(1L);

        userListener.registerUser(update);

        Mockito.verify(userService, never()).save(ArgumentMatchers.any(User.class));
        Mockito.verify(telegramClient, never()).execute(any(SendMessage.class));
    }

    @Test
    void startBotWithUnregisteredUser_PosTC() throws TelegramApiException {
        Mockito.when(update.getMessage().getChatId()).thenReturn(12345L);
        Mockito.when(update.getMessage().getFrom().getUserName()).thenReturn("Username");

        userListener.registerUser(update);

        Mockito.verify(userService).save(ArgumentMatchers.any(User.class));
        Mockito.verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            return m.getText().equals("Hi, Username! I am you car assistant that will help you to find your dream car " +
                    "in the fastest way possible. Just set up filter for yourself. That's all :)");
        }));

        assertThat(findByChatId(12345L))
                .isPresent();
    }

    @Test
    void setFilter_PosTC() throws TelegramApiException {
        Mockito.when(update.getMessage().getChatId()).thenReturn(1L);

        userListener.setFilter(update);

        assertThat(findByChatId(1L))
                .isPresent()
                .get()
                .extracting(User::getRedirectTo)
                .isEqualTo(UPLOAD_URL);

        Mockito.verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            List<InlineKeyboardRow> replyKeyboard = ((InlineKeyboardMarkup) m.getReplyMarkup()).getKeyboard();
            return m.getText().equals("Click the button below to open otomoto page:") &&
                    replyKeyboard.size() == 1 && replyKeyboard.getFirst().size() == 1 &&
                    replyKeyboard.getFirst().getFirst().getText().equals("Open otomoto") &&
                    replyKeyboard.getFirst().getFirst().getUrl().equals("https://www.otomoto.pl/osobowe?search%5Badvanced_search_expanded%5D=true");
        }));
    }

    @Test
    void uploadUrl_PosTC() throws TelegramApiException {
        long chatId = 4L;
        String url = "https://www.otomoto.pl/osobowe/audi--bmw/od-2015?search%5Bfilter_float_mileage%3Afrom%5D=75000&search%5Bfilter_float_mileage" +
                "%3Ato%5D=170000&search%5Bfilter_float_price%3Afrom%5D=2000&search%5Bfilter_float_price%3Ato%5D=35000&search%5Bfilter_float_year%3At" +
                "o%5D=2020&search%5Badvanced_search_expanded%5D=true";
        Mockito.when(update.getMessage().getChatId()).thenReturn(chatId);

        userListener.uploadUrl(url, update);

        Optional<User> user = findByChatId(chatId);

        assertThat(user)
                .isPresent()
                .get()
                .extracting(User::getSearchUrl, User::getRedirectTo)
                .containsExactly(url, CONFIRM_FILTER);

        assertThat(findByUserId(user.get().getId()))
                .isPresent()
                .get()
                .extracting(
                    UserFilter::isConfirmed,
                    UserFilter::getMileageFrom,
                    UserFilter::getMileageTo,
                    UserFilter::getYearFrom,
                    UserFilter::getYearTo,
                    UserFilter::getPriceStart,
                    UserFilter::getPriceEnd,
                    filter -> filter.getCarBrands().stream().map(CarBrand::getName).toList(),
                    filter -> filter.getCarModels().stream().map(CarModel::getName).toList(),
                    filter -> filter.getGenerations().stream().map(Generation::getName).toList(),
                    filter -> filter.getFuelTypes().stream().map(FuelType::getName).toList()
                ).containsExactly(
                false,
                    75000,
                    170000,
                    2015,
                    2020,
                    2000L,
                    35000L,
                    List.of("BMW", "Audi"),
                    emptyList(),
                    emptyList(),
                    emptyList()
                );

        verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            List<KeyboardRow> replyRow = ((ReplyKeyboardMarkup) m.getReplyMarkup()).getKeyboard();
            return m.getText().startsWith("You selected the following filters for yourself, are they right? Please type 'yes' or 'no' in the response.\n\n") &&
                    replyRow.size() == 1 && replyRow.getFirst().size() == 2 &&
                    replyRow.getFirst().getFirst().getText().equals("Yes") &&
                    replyRow.getFirst().get(1).getText().equals("No");
        }));
    }

    @Test
    void uploadUrlWithInvalidUrl_NegTC() {
        long chatId = 4L;
        String url = "https://www.testdomain.pl/osobowe/audi--bmw/od-2015?search%5Bfilter_float_mileage%3Afrom%5D=75000&search%5Bfilter_float_mileage" +
                "%3Ato%5D=170000&search%5Bfilter_float_price%3Afrom%5D=2000&search%5Bfilter_float_price%3Ato%5D=35000&search%5Bfilter_float_year%3At" +
                "o%5D=2020&search%5Badvanced_search_expanded%5D=true";
        Mockito.when(update.getMessage().getChatId()).thenReturn(chatId);

        assertThatThrownBy(() -> userListener.uploadUrl(url, update))
                .isInstanceOf(InvalidSearchUrlException.class);

        verify(userService, never()).save(any(User.class));
        verify(userService, never()).save(any(UserFilter.class));
    }

    @Test
    void uploadUrlWithNotRedirectedUser_NegTC() {
        long chatId = 1L;
        String url = "https://www.otomoto.pl/osobowe/audi--bmw/od-2015?search%5Bfilter_float_mileage%3Afrom%5D=75000&search%5Bfilter_float_mileage" +
                "%3Ato%5D=170000&search%5Bfilter_float_price%3Afrom%5D=2000&search%5Bfilter_float_price%3Ato%5D=35000&search%5Bfilter_float_year%3At" +
                "o%5D=2020&search%5Badvanced_search_expanded%5D=true";
        Mockito.when(update.getMessage().getChatId()).thenReturn(chatId);

        assertThatThrownBy(() -> userListener.uploadUrl(url, update))
                .isInstanceOf(UserIsNotRedirectedException.class);

        verify(userService, never()).save(any(User.class));
        verify(userService, never()).save(any(UserFilter.class));
    }

    @Test
    void confirmFilterWithYesAnswer_PosTC() throws TelegramApiException {
        long chatId = 5L;
        Mockito.when(update.getMessage().getChatId()).thenReturn(chatId);

        userListener.confirmFilter("yes", update);

        Optional<User> user = findByChatId(chatId);
        assertThat(user)
                .isPresent()
                .get()
                .extracting(User::getRedirectTo)
                .isNull();

        assertThat(findByUserId(user.get().getId()))
                .isPresent()
                .get()
                .extracting(UserFilter::isConfirmed)
                .isEqualTo(true);

        verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            return m.getText().equals("You filters was successfully saved.") &&
                    m.getReplyMarkup() instanceof ReplyKeyboardRemove;
        }));
    }

    @Test
    void confirmFilterWithNoAnswer_NegTC() throws TelegramApiException {
        long chatId = 5L;
        Mockito.when(update.getMessage().getChatId()).thenReturn(chatId);

        userListener.confirmFilter("no", update);

        Optional<User> user = findByChatId(chatId);
        assertThat(user)
                .isPresent()
                .get()
                .extracting(User::getRedirectTo, User::getSearchUrl)
                .containsExactly(null, null);

        assertThat(findByUserId(user.get().getId()))
                .isEmpty();

        verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            return m.getText().equals("Sorry, our app is in development now. Try again.") &&
                    m.getReplyMarkup() instanceof ReplyKeyboardRemove;
        }));
    }

    @Test
    void confirmFilterWithInvalidAnswer_NegTC() throws TelegramApiException {
        long chatId = 5L;
        Mockito.when(update.getMessage().getChatId()).thenReturn(chatId);

        userListener.confirmFilter("answer", update);

        Optional<User> user = findByChatId(chatId);
        assertThat(user)
                .isPresent()
                .get()
                .extracting(User::getRedirectTo)
                .isEqualTo("/confirm_filter");

        assertThat(findByUserId(user.get().getId()))
                .isPresent()
                .get()
                .extracting(UserFilter::isConfirmed)
                .isEqualTo(false);

        verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            return m.getText().equals("Please type 'yes' or 'no'.");
        }));
    }

    @Test
    void showExistingFilter_PosTC() throws TelegramApiException {
        long chatId = 6L;
        Mockito.when(update.getMessage().getChatId()).thenReturn(chatId);

        userListener.showFilter(update);

        verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            return m.getText().startsWith("Your current filters are:\n\n") &&
                    m.getParseMode().equals("Markdown");
        }));
    }

    @Test
    void showNonExistingFilter_PosTC() throws TelegramApiException {
        long chatId = 1L;
        Mockito.when(update.getMessage().getChatId()).thenReturn(chatId);

        userListener.showFilter(update);

        verify(telegramClient).execute((SendMessage) argThat(message -> {
            SendMessage m = (SendMessage) message;
            return m.getText().equals("You don't have any filters now.");
        }));
    }

    private Optional<User> findByChatId(long chatId){
        return userRepository.findAll().stream()
                .filter(u -> u.getChatId() == chatId)
                .findFirst();
    }

    private Optional<UserFilter> findByUserId(long userId) {
        return Optional.ofNullable(userFilterRepository.findByUser_Id(userId));
    }
}