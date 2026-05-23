package br.com.challenge.controller.mapper;

import br.com.challenge.controller.dto.BlogPostDetailDto;
import br.com.challenge.controller.dto.BlogPostRequestDto;
import br.com.challenge.controller.dto.BlogPostSummaryDto;
import br.com.challenge.domain.BlogPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {CommentMapper.class})
public interface BlogPostMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "comments", ignore = true)
  @Mapping(target = "commentCount", ignore = true)
  BlogPost toEntity(BlogPostRequestDto dto);

  BlogPostDetailDto toDetailDto(BlogPost blogPost);

  BlogPostSummaryDto toSummaryDto(BlogPost blogPost);
}
