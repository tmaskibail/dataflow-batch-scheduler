/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tmaskibail.appengine.scheduler.model;

import java.io.Serializable;

public class Parameters implements Serializable {
    private String javascriptTextTransformFunctionName;
    private String JSONPath;
    private String javascriptTextTransformGcsPath;
    private String inputFilePattern;
    private String outputTable;
    private String bigQueryLoadingTemporaryDirectory;

    public String getJavascriptTextTransformFunctionName() {
        return javascriptTextTransformFunctionName;
    }

    public void setJavascriptTextTransformFunctionName(String javascriptTextTransformFunctionName) {
        this.javascriptTextTransformFunctionName = javascriptTextTransformFunctionName;
    }

    public String getJSONPath() {
        return JSONPath;
    }

    public void setJSONPath(String JSONPath) {
        this.JSONPath = JSONPath;
    }

    public String getJavascriptTextTransformGcsPath() {
        return javascriptTextTransformGcsPath;
    }

    public void setJavascriptTextTransformGcsPath(String javascriptTextTransformGcsPath) {
        this.javascriptTextTransformGcsPath = javascriptTextTransformGcsPath;
    }

    public String getInputFilePattern() {
        return inputFilePattern;
    }

    public void setInputFilePattern(String inputFilePattern) {
        this.inputFilePattern = inputFilePattern;
    }

    public String getOutputTable() {
        return outputTable;
    }

    public void setOutputTable(String outputTable) {
        this.outputTable = outputTable;
    }

    public String getBigQueryLoadingTemporaryDirectory() {
        return bigQueryLoadingTemporaryDirectory;
    }

    public void setBigQueryLoadingTemporaryDirectory(String bigQueryLoadingTemporaryDirectory) {
        this.bigQueryLoadingTemporaryDirectory = bigQueryLoadingTemporaryDirectory;
    }
}
