package it.uniroma1.lcl.crucy.utils;

public class AssetWrapper {

    private Class type;
    private String path;

    public <T> AssetWrapper( String path, Class<T> type) {
        this.type = type;
        this.path = path;
    }

    public Class getType() {
        return this.type;
    }

    public String getPath() {
        return this.path;
    }
}
