/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lufficc.ishuhui.data.source.local;

import android.provider.BaseColumns;

/**
 * The contract used for the db to save the tasks locally.
 */
public final class FilesPersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private FilesPersistenceContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class FileEntry implements BaseColumns {
        public static final String TABLE_NAME = "files";
        public static final String COLUMN_NAME_ENTRY_ID = "file_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_LOCAL_PATH = "local_path";
        public static final String COLUMN_NAME_CHAPTER_ID = "chapter_id";
        public static final String COLUMN_NAME_COMIC_ID = "comic_id";
        public static final String COLUMN_NAME_CHAPTER_NAME = "chapter_name";
        public static final String COLUMN_NAME_COMIC_NAME = "comic_name";
    }
}
