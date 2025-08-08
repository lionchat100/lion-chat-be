package com.lion.be.feed.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FeedWriterDto {
    private String name;
    private Long id;
    private String profileImageUrl;
}
