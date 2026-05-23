package br.com.challenge.controller.mapper;

import br.com.challenge.controller.dto.CommentDto;
import br.com.challenge.controller.dto.CommentRequestDto;
import br.com.challenge.domain.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "blogPost", ignore = true)
  Comment toEntity(CommentRequestDto dto);

  CommentDto toDto(Comment comment);
}
