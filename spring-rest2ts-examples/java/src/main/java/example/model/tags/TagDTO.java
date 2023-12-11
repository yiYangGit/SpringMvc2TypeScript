package example.model.tags;

import example.model.core.BaseDTO;

public class TagDTO extends BaseDTO {
    private String name;

    public String getName() {
        return name;
    }

    public TagDTO setName(String name) {
        this.name = name;
        return this;
    }
}
