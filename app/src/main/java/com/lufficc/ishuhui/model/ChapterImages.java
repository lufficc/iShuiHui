package com.lufficc.ishuhui.model;

import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lufficc on 2016/11/6.
 */
@Table("chapter_images")
public class ChapterImages {
    @PrimaryKey(AssignType.BY_MYSELF)
    private String chapterId;

    private String comicName;
    private String comicId;
    private String chapterName;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
    private List<FileEntry> images;

    public List<FileEntry> getImages() {
        return images;
    }

    public void setImages(List<FileEntry> images) {
        this.images = images;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getComicId() {
        return comicId;
    }

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }


}
