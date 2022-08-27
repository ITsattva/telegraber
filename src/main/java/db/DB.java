package db;

public interface DB<T> {
    void putIntoDB(T content);
    boolean checkRepetition(T content);

}
