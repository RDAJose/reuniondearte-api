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

    @Test
    void uploadAudioAndVideoFilesAddAssociationsWithoutReplacingPreviousMediaFiles() {
        Article article = article();
        when(articles.findWithRelationsById(1L)).thenReturn(Optional.of(article));
        when(articleMedia.countByArticleIdAndRole(1L, "audio")).thenReturn(0, 1);
        when(articleMedia.countByArticleIdAndRole(1L, "video")).thenReturn(0);
        when(mediaStorage.storeArticleAudio(any(), any(), any())).thenReturn(
                storedFile("articles/body-test/audios/audio-1.mp3", "http://localhost/media/audio-1.mp3", "audio-1.mp3", "audio/mpeg"),
                storedFile("articles/body-test/audios/audio-2.mp3", "http://localhost/media/audio-2.mp3", "audio-2.mp3", "audio/mpeg")
        );
        when(mediaStorage.storeArticleVideo(any(), any(), any()))
                .thenReturn(storedFile("articles/body-test/videos/video-1.mp4", "http://localhost/media/video-1.mp4", "video-1.mp4", "video/mp4"));
        when(mediaAssets.save(any(MediaAsset.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(articleMedia.save(any(ArticleMedia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        controller.uploadAudioFile(1L, audioFile("one.mp3"), "Audio 1", null, null, null, null);
        controller.uploadAudioFile(1L, audioFile("two.mp3"), "Audio 2", null, null, null, null);
        controller.uploadVideoFile(1L, videoFile("one.mp4"), "Video 1", null, null, null, null);

        ArgumentCaptor<ArticleMedia> associationCaptor = ArgumentCaptor.forClass(ArticleMedia.class);
        verify(articleMedia, never()).deleteByArticleIdAndRoleIn(any(), anyList());
        verify(articleMedia, times(3)).save(associationCaptor.capture());
        assertThat(associationCaptor.getAllValues())
                .extracting(ArticleMedia::getRole)
                .containsExactly("audio", "audio", "video");
        assertThat(associationCaptor.getAllValues())
                .extracting(association -> association.getMediaAsset().getPublicUrl())
                .containsExactly(
                        "http://localhost/media/audio-1.mp3",
                        "http://localhost/media/audio-2.mp3",
                        "http://localhost/media/video-1.mp4"
                );
    }

    @Test
    void removeMediaFileDeletesOnlyTheArticleAssociation() {
        Article article = article();
        MediaAsset mediaAsset = new MediaAsset();
        mediaAsset.applyStoredMediaFile(
                "audio",
                "local",
                "articles/body-test/audios/audio-1.mp3",
                "http://localhost/media/audio-1.mp3",
                "audio-1.mp3",
                "audio/mpeg",
                3L,
                "Audio 1",
                null,
                null,
                null,
                null
        );
        ArticleMedia association = ArticleMedia.create(article, mediaAsset, "audio", 0);
        when(articles.findWithRelationsById(1L)).thenReturn(Optional.of(article));
        when(articleMedia.findByIdAndArticleIdAndRoleIn(9L, 1L, List.of("audio", "video"))).thenReturn(Optional.of(association));

        controller.removeMediaFile(1L, 9L);

        verify(articleMedia).delete(association);
        verify(mediaAssets, never()).delete(any(MediaAsset.class));
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

    private MockMultipartFile audioFile(String filename) {
        return new MockMultipartFile("file", filename, "audio/mpeg", new byte[] {1, 2, 3});
    }

    private MockMultipartFile videoFile(String filename) {
        return new MockMultipartFile("file", filename, "video/mp4", new byte[] {1, 2, 3});
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

    private MediaStorageService.StoredFile storedFile(String storagePath, String publicUrl, String filename, String mimeType) {
        return new MediaStorageService.StoredFile(
                "local",
                storagePath,
                publicUrl,
                filename,
                mimeType,
                3L
        );
    }
}
