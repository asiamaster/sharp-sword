package com.dili.ss.mbg;

import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Document;

/**
 * Created by asiam on 2017/4/11 0011.
 */
public class DocumentExt extends Document {

    @Override
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();
        if(this.getPublicId() != null && this.getSystemId() != null) {
            OutputUtilities.newLine(sb);
            sb.append("<!DOCTYPE ");
            sb.append(this.getRootElement().getName());
            sb.append(" PUBLIC \"");
            sb.append(this.getPublicId());
            sb.append("\" \"");
            sb.append(this.getSystemId());
            sb.append("\">");
        }

        OutputUtilities.newLine(sb);
        sb.append(this.getRootElement().getFormattedContent(0));
        return sb.toString();
    }
}
