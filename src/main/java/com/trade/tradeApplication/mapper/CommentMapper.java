package com.trade.tradeApplication.mapper;

import com.trade.tradeApplication.entity.CommentEntity;
import com.trade.tradeApplication.model.Comment;
import com.trade.tradeApplication.model.CreateOrUpdateComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CommentMapper {
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.image", target = "authorImage")
    Comment toDto(CommentEntity comment);
    @Mapping(source = "author", target = "author.id")
    CommentEntity toEntityFromComment(Comment dto);

    CommentEntity toEntityFromCreateOrUpdateComment(CreateOrUpdateComment dto);


}
