package example.model;

import example.model.core.BaseDTO;

public class GenericReference<T extends BaseDTO> extends BaseDTO{
    public T reference;
}

class ManufacturerReferenceDTO extends BaseDTO{
    GenericReference<ManufacturerDTO> manufacturerReference;
}



