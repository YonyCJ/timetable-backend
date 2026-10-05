package config;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class Page<T> {
    private List<T> content;
    private long totalItems;
    private int page;
    private int pageSize;
}
