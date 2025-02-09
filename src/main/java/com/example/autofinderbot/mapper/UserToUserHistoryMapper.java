package com.example.autofinderbot.mapper;

import com.example.autofinderbot.domain.User;
import com.example.autofinderbot.domain.UserHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface UserToUserHistoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "id")
    UserHistory map(User user);
}
