package response;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "from")
public class ListContainer<T> {
    List<T> list;
}
