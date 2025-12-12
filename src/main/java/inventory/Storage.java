package inventory;

import java.util.ArrayList;
import java.util.List;

// Generic Storage class untuk menyimpan item dengan type-safe
public class Storage<T> {
    private List<T> items;
    private int capacity;

    public Storage() {
        this.items = new ArrayList<>();
        this.capacity = -1; // unlimited
    }

    public Storage(int capacity) {
        this.items = new ArrayList<>();
        this.capacity = capacity; // ada kapasitas
    }

    public boolean addItem(T item) {
        if (item == null) {
            return false;
        }

        if (capacity > 0 && items.size() >= capacity) {
            return false; // Storage penuh
        }

        return items.add(item);
    }

    public T removeItem() {
        if (items.isEmpty()) {
            return null;
        }
        return items.remove(items.size() - 1);
    }

    public T removeItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.remove(index);
    }

    // return item terakhir
    public T peekItem() {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(items.size() - 1);
    }

    // return sesuai index
    public T peekItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    // return semua item
    public List<T> getAllItems() {
        return new ArrayList<>(items);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isFull() {
        if (capacity <= 0) {
            return false; // Unlimited capacity
        }
        return items.size() >= capacity;
    }

    public int size() {
        return items.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean contains(T item) {
        return items.contains(item);
    }

    public boolean remove(T item) {
        return items.remove(item);
    }
}

