package db;

public interface DB {

    void putIntoDB(String content);
    boolean checkRepetition(String content);

}
