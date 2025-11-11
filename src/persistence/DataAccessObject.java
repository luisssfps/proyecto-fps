package persistence;

import java.util.List;

public interface DataAccessObject<T> {
  void saveAll(List<T> items);

  List<T> loadAll();
}
