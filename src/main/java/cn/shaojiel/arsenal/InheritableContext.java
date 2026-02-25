package cn.shaojiel.arsenal;

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

    public <T> T get(final String name) {
        return (T) this.testContexts.get().get(name);
    }

    public <T> T set(final String name, final T object) {
        this.testContexts.get().put(name, object);
        return object;
    }

    public <T> T get(final String name, final Class<T> classType) {
        return classType.cast(this.testContexts.get().get(name));
    }

    public void reset() {
        this.testContexts.get().clear();
    }

}
