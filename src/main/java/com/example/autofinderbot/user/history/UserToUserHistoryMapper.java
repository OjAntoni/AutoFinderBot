package com.example.autofinderbot.user.history;

import com.example.autofinderbot.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface UserToUserHistoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "id")
    UserHistory map(User user);
}
