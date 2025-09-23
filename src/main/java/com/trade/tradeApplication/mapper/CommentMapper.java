package com.trade.tradeApplication.mapper;

import com.trade.tradeApplication.entity.CommentEntity;
import com.trade.tradeApplication.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CommentMapper {
    @Mapping(source = "author.id", target = "author")
    Comment toDto(CommentEntity comment);
    @Mapping(source = "author", target = "author.id")
    CommentEntity toEntity(Comment dto);
}
