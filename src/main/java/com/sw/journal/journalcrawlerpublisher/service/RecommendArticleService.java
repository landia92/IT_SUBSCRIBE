package com.sw.journal.journalcrawlerpublisher.service;

import com.sw.journal.journalcrawlerpublisher.domain.*;
import com.sw.journal.journalcrawlerpublisher.repository.CategoryRepository;
import com.sw.journal.journalcrawlerpublisher.repository.OurArticleRepository;
import com.sw.journal.journalcrawlerpublisher.repository.TagRepository;
import com.sw.journal.journalcrawlerpublisher.repository.UserFavoriteCategoryRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// wild-mantle 2024-07-08
@Getter @Setter
@Service
public class RecommendArticleService {

    @Autowired
    private OurArticleRepository ourArticleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserFavoriteCategoryRepository userFavoriteCategoryRepository;

    private OurArticleService ourArticleService;

    // 최신 기사를 주어진 개수만큼 조회
    public List<OurArticle> findRecentArticles(int limit) {
        // PageRequest.of(0, limit)을 통해 첫 번째 페이지에서 주어진 개수만큼 기사 조회
        // 조회된 결과에서 기사 리스트 반환
        return ourArticleRepository.findAll(PageRequest.of(0, limit)).getContent();
    }

    // 사용자 선호 카테고리의 기사 조회
    public List<OurArticle> findByUserFavoriteCategories(Member member) {
        // 사용자 선호 카테고리 리스트 조회
        List<UserFavoriteCategory> favoriteCategories = userFavoriteCategoryRepository.findByMember(member);
        // 선호 카테고리 리스트에서 카테고리 객체만 추출하여 리스트로 변환
        List<Category> categories = favoriteCategories.stream()
                .map(UserFavoriteCategory::getCategory) // UserFavoriteCategory에서 Category 객체 추출
                .collect(Collectors.toList()); // 추출한 Category 객체를 리스트로 수집
        // 해당 카테고리에 속한 기사를 조회하여 반환
        return ourArticleRepository.findByCategories(categories);
    }

    // 사용자 선호 카테고리의 기사 중에서 최신 기사를 주어진 개수만큼 조회
    public List<OurArticle> findTopByUserFavoriteCategoriesOrderByPostDateDesc(Member member, int limit) {
        // 사용자 선호 카테고리 리스트 조회
        List<UserFavoriteCategory> favoriteCategories = userFavoriteCategoryRepository.findByMember(member);
        // 선호 카테고리 리스트에서 카테고리 객체만 추출하여 리스트로 변환
        List<Category> categories = favoriteCategories.stream()
                .map(UserFavoriteCategory::getCategory) // UserFavoriteCategory에서 Category 객체 추출
                .collect(Collectors.toList()); // 추출한 Category 객체를 리스트로 수집
        // 해당 카테고리에 속한 기사 중 최신 기사를 주어진 개수만큼 조회하여 반환
        return ourArticleRepository.findTopByCategoriesOrderByPostDate(categories, PageRequest.of(0, limit));
    }
}
