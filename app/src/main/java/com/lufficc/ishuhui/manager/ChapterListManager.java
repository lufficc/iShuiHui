package com.lufficc.ishuhui.manager;

import android.support.annotation.Nullable;

import com.lufficc.ishuhui.model.Chapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lcc_luffy on 2016/5/27.
 */
public class ChapterListManager {
    private static ChapterListManager instance;
    private List<Chapter> chapters;
    private int currentIndex;

    private ChapterListManager() {
    }

    public static ChapterListManager instance() {
        if (instance == null)
            instance = new ChapterListManager();
        return instance;
    }

    public List<?> getChapters() {
        return chapters;
    }

    public void setChapters(Collection<Chapter> chapters, int currentIndex) {
        if (this.chapters == null)
            this.chapters = new ArrayList<>();
        this.chapters.clear();
        this.chapters.addAll(chapters);
        this.currentIndex = currentIndex;
    }

    @Nullable
    public Chapter nextChapter() {
        if (currentIndex > 0 && !chapters.isEmpty()) {
            currentIndex--;
            return chapters.get(currentIndex);
        }
        return null;
    }

    public void clear()
    {
        if (this.chapters != null)
            this.chapters.clear();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
