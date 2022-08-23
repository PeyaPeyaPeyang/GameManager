package tokyo.peya.plugins.gamemanager;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import tokyo.peya.plugins.gamemanager.seed.GameStartCause;

@Getter
class CountdownProvider
{
    private final Game game;

    private boolean countdownRunning;

    private final int countdownTime;

    private int countdownTimeRemaining;

    @Getter(AccessLevel.NONE)
    private final CountdownLogic countdownRunnable;

    public CountdownProvider(Game game, int countdownTime)
    {
        this.game = game;

        this.countdownRunning = false;
        this.countdownTime = countdownTime;

        this.countdownRunnable = new CountdownLogic(this);
    }

    public void scheduleGameStart()
    {
        if (this.countdownRunning)
            throw new IllegalStateException("Countdown is already started");

        this.countdownRunning = true;
        this.countdownTimeRemaining = this.countdownTime;

        this.countdownRunnable.runTaskTimer(this.game.getPlugin(), 0, 20);
    }

    public void cancelGameStart()
    {
        if (!this.countdownRunning)
            throw new IllegalStateException("Countdown is not started");

        this.countdownRunning = false;

        if (!this.countdownRunnable.isCancelled())
            this.countdownRunnable.cancel();
    }

    public void skipCountdown()
    {
        if (!this.countdownRunning)
            throw new IllegalStateException("Countdown is not started");

        this.cancelGameStart();

        this.game.start();
    }


    private static class CountdownLogic extends BukkitRunnable
    {
        private final CountdownProvider provider;

        public CountdownLogic(CountdownProvider provider)
        {
            this.provider = provider;
        }

        @Override
        public void run()
        {
            if (!this.provider.countdownRunning)
                return;

            int countdownTimeRemaining = this.provider.countdownTimeRemaining;


            if (countdownTimeRemaining <= 0)
            {
                this.provider.countdownRunning = false;
                this.cancel();
                this.provider.game.start(GameStartCause.COUNTDOWN);
                return;
            }

            this.provider.game.dispatchOnStartCountdown(countdownTimeRemaining);

            this.provider.countdownTimeRemaining--;
        }
    }
}
