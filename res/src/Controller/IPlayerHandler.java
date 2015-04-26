package Controller;

/**
 * Created by weitao on 4/23/15.
 */
public interface IPlayerHandler {
    Move getMove();

    void moveSuccessfullyExecuted(Move move);
}
