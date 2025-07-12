package team9.demo.util.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team9.demo.model.media.FileCategory;

@Component
public class StringToFileCategoryConverter implements Converter<String, FileCategory> {

    @Override
    public FileCategory convert(String source) {
        return FileCategory.valueOf(source.toUpperCase());
    }
}