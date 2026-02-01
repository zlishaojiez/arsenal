package cn.shaojiel.util;

import java.util.HashMap;
import java.util.Map;

public enum InheritableContext {
    INHERITABLE_CONTEXT;

    private final InheritableThreadLocal<Map<String, Object>> testContexts = new InheritableThreadLocal<>(){
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    public <T> T get(String name) {
        return (T) this.testContexts.get().get(name);
    }

    public <T> T set(String name, T object) {
        this.testContexts.get().put(name, object);
        return object;
    }

    public <T> T get(String name, Class<T> classType) {
        return classType.cast(this.testContexts.get().get(name));
    }

    public void reset() {
        this.testContexts.get().clear();
    }

}
