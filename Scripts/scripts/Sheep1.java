package scripts;
import java.util.Locale;



import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.MessageListener;
import org.rscdaemon.client.bot.Script;

public final class Sheep1 extends Script implements MessageListener {
    
    private static final int GATE = 60;
    private static final int BANK_X = 580;
    private static final int BANK_Y = 571;
    private static final int FIELD_X = 557;
    private static final int FIELD_Y = 543;

    private static final int WOOL = 145;
    private static final int SHEARS = 144;
    private static final int GNOME_BALL = 981;
    private static final int SHEEP = 2;
    private long start_time;
    private long click_time;
    private long bank_time;
    private int total_fails;
    private int total_success;
    private int banked_count;
    
    private PathWalker pw;
    private PathWalker.Path to_bank;
    private PathWalker.Path from_bank;

    public Sheep1(Extension ex) {
        super(ex);
    }

    @Override
    public boolean init() {
        pw = new PathWalker(this.e);
        pw.scriptInit();
        to_bank = pw.calcPath(FIELD_X, FIELD_Y, BANK_X, BANK_Y);
        from_bank = pw.calcPath(BANK_X, BANK_Y, FIELD_X, FIELD_Y);
        bank_time = -1L;
        start_time = -1L;
        click_time = -1L;
        total_fails = 0;
        total_success = 0;
        banked_count = 0;
        System.out.println("Start in sheep field north of East Ardougne");
        return true;
    }

    @Override
    public long run() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (isQuestMenu()) {
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(2000, 3000);
        }
        if (isBanking()) {
            int count = getInventoryCount(WOOL);
            if (count > 0) {
                banked_count += count;
                deposit(WOOL, count);
                return random(1500, 2000);
            }
            if (getInventoryIndex(SHEARS) == -1) {
                stopScript();
                System.out.println("you have no shears");
                return random(1500, 2000);
            }
            pw.setPath(from_bank);
            closeBank();
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
//        int ball = getInventoryIndex(GNOME_BALL);
//        if (ball != -1) {
//            System.out.println("Gnome ball!");
//            dropItem(ball);
//            return random(1200, 2000);
//        }
        if (pw.walkPath()) return 0;
        if (click_time != -1L) {
            if (System.currentTimeMillis() >= click_time) {
                click_time = -1L;
            }
            return 0;
        }
        if (getInventoryCount() == 30) {
            if (isAtApproxCoords(BANK_X, BANK_Y, 15)) {
                int[] banker = getNpcById(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    return random(3000, 3500);
                }
                return random(600, 1000);
            } else {
                pw.setPath(to_bank);
                return random(800, 1200);
            }
        } else {
            int shears = getInventoryIndex(SHEARS);
            if (shears == -1) {
                pw.setPath(to_bank);
                return random(800, 1200);
            }
            if (_invalidCoords(getX(), getY())) {
//                if (!isWalking()) {
                    int[] gate = getObjectById(GATE);
                    if (gate[0] != -1 && distanceTo(gate[1], gate[2]) < 16) {
                        atObject(gate[1], gate[2]);
                    } else {
                        _walkApprox(553, 542, 2);
                    }
//                }
                return random(1500, 2500);
            }
            int[] sheep = getNpcById(SHEEP);
            if (_invalidCoords(sheep[1], sheep[2])) {
//                if (!isWalking()) {
                    _walkApprox(553, 542, 2);
//                }
                return random(1500, 2500);
            }
            if (sheep[0] != -1) {
                useItemOnNpc(SHEARS, sheep[0]);
            }
            return random(600, 1000);
        }
    }
    
    private boolean _invalidCoords(int x, int y) {
        if ((x != getX() || y != getY()) && !isReachable(x, y)) {
            return true;
        }
        return x > 557 || x < 550 || y > 554 || y < 536;
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 7200) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 3600 && secs < 7200) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
    
    private void _walkApprox(int x, int y, int range) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-range, range);
            dy = y + random(-range, range);
            if ((++loop) > 100) return;
        } while (!isReachable(dx, dy));
        walkTo(dx, dy);
    }
    
    private int _end(String reason) {
        System.out.println(reason);
        _printOut();
        stopScript(); 
        return 0;
    }
    
    private void _printOut() {
        System.out.println("Runtime: " + _getRuntime());
        System.out.println("Total fails: " + total_fails);
        System.out.println("Total success: " + total_success);
        System.out.println("Banked count: " + banked_count);
    }

	@Override
	public void onMessage(String str, int type, int status) {
		
		
        str.toLowerCase();
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("attempt")) {
            click_time = System.currentTimeMillis() + random(5000, 7000);
        } else if (str.contains("get away")) {
            click_time = System.currentTimeMillis() + random(100, 200);
            ++total_fails;
        } else if (str.contains("get some")) {
            click_time = System.currentTimeMillis() + random(100, 200);
            ++total_success;
        }
	}
}