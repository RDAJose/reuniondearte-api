package com.reuniondearte.api.admin;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.media.ArticleMedia;
import com.reuniondearte.api.media.ArticleMediaRepository;
import com.reuniondearte.api.media.MediaAsset;
import com.reuniondearte.api.media.MediaAssetRepository;
import com.reuniondearte.api.media.MediaStorageService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminArticleMediaControllerTest {

    private final ArticleRepository articles = mock(ArticleRepository.class);
    private final MediaAssetRepository mediaAssets = mock(MediaAssetRepository.class);
    private final ArticleMediaRepository articleMedia = mock(ArticleMediaRepository.class);
    private final MediaStorageService mediaStorage = mock(MediaStorageService.class);
    private final AdminArticleMediaController controller = new AdminArticleMediaController(
            articles,
            mediaAssets,
            articleMedia,
            mediaStorage
    );

    @Test
    void uploadBodyImageAddsAssociationsWithoutReplacingExistingBodyImages() {
        Article article = article();
        when(articles.findWithRelationsById(1L)).thenReturn(Optional.of(article));
        when(articleMedia.countByArticleIdAndRole(1L, "body")).thenReturn(0, 1);
        when(mediaStorage.storeArticleImage(any(), any(), any())).thenReturn(
                storedImage("articles/body-test/body-1.jpg", "http://localhost/media/body-1.jpg", "body-1.jpg"),
                storedImage("articles/body-test/body-2.jpg", "http://localhost/media/body-2.jpg", "body-2.jpg")
        );
        when(mediaAssets.save(any(MediaAsset.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(articleMedia.save(any(ArticleMedia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        controller.uploadBodyImage(1L, file("one.jpg"), "Alt 1", null, null, null, null);
        controller.uploadBodyImage(1L, file("two.jpg"), "Alt 2", null, null, null, null);

        ArgumentCaptor<ArticleMedia> associationCaptor = ArgumentCaptor.forClass(ArticleMedia.class);
        verify(articleMedia, never()).deleteByArticleIdAndRoleIn(any(), anyList());
        verify(articleMedia, times(2)).countByArticleIdAndRole(1L, "body");
        verify(articleMedia, times(2)).save(associationCaptor.capture());
        List<ArticleMedia> savedAssociations = associationCaptor.getAllValues();
        assertThat(savedAssociations).hasSize(2);
        assertThat(savedAssociations).allSatisfy(association -> assertThat(association.getRole()).isEqualTo("body"));
        assertThat(savedAssociations)
                .extracting(association -> association.getMediaAsset().getPublicUrl())
                .containsExactly("http://localhost/media/body-1.jpg", "http://localhost/media/body-2.jpg");
    }

    @Test
    void importBodyImageAddsAssociationWithoutReplacingExistingBodyImages() {
        Article article = article();
        when(articles.findWithRelationsById(1L)).thenReturn(Optional.of(article));
        when(articleMedia.countByArticleIdAndRole(1L, "body")).thenReturn(2);
        when(mediaStorage.importArticleImage("body-test", "body-3", "https://example.com/body-3.jpg"))
                .thenReturn(storedImage("articles/body-test/body-3.jpg", "http://localhost/media/body-3.jpg", "body-3.jpg"));
        when(mediaAssets.save(any(MediaAsset.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(articleMedia.save(any(ArticleMedia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        controller.importBodyImage(
                1L,
                new AdminImageImportRequest(
                        "https://example.com/body-3.jpg",
                        "Alt 3",
                        "Caption 3",
                        "Credit 3",
                        "https://example.com/source",
                        "Licensed for press use"
                )
        );

        ArgumentCaptor<ArticleMedia> associationCaptor = ArgumentCaptor.forClass(ArticleMedia.class);
        verify(articleMedia, never()).deleteByArticleIdAndRoleIn(any(), anyList());
        verify(articleMedia).save(associationCaptor.capture());
        ArticleMedia savedAssociation = associationCaptor.getValue();
        assertThat(savedAssociation.getRole()).isEqualTo("body");
        assertThat(savedAssociation.getMediaAsset().getPublicUrl()).isEqualTo("http://localhost/media/body-3.jpg");
    }

    private Article article() {
        Article article = new Article();
        ReflectionTestUtils.setField(article, "id", 1L);
        ReflectionTestUtils.setField(article, "slug", "body-test");
        return article;
    }

    private MockMultipartFile file(String filename) {
        return new MockMultipartFile("file", filename, "image/jpeg", new byte[] {1, 2, 3});
    }

    private MediaStorageService.StoredImage storedImage(String storagePath, String publicUrl, String filename) {
        return new MediaStorageService.StoredImage(
                "local",
                storagePath,
                publicUrl,
                filename,
                "image/jpeg",
                3L,
                100,
                100
        );
    }
}
