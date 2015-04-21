package scripts;


import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.MessageListener;
import org.rscdaemon.client.bot.Script;

public class Cast4 extends Script implements MessageListener {

	private int castWaitTime = 0;
	private long lastCast;

	public Cast4(Extension e) {
		super(e);

	}
	
	//18
	//28
	private static final int SPELL_INDEX = 17; //6= fire strike
	
	@Override
	public long run() {
		
		if(isSleeping()) {
			return 1000;
		} else if(getFatigue() > 95 || !hasRunes(SPELL_INDEX)) {
			stopScript();
		} else {
			int[] npc = getNpcByIdIgnoreReachable(262);
			if(npc[0] != -1 && System.currentTimeMillis() - lastCast > castWaitTime) {
				lastCast = System.currentTimeMillis();
				castOnNpc(npc[0], SPELL_INDEX);
				return random(500, 750);
			}
		}
		return random(250, 550);
	}

	@Override
	public void onMessage(String message, int type, int status) {
		if(type == 3 && status == 0) {
			if(message.equals("Cast spell successfully")) {
				castWaitTime = random(1250, 1500);
			} else if(message.equals("The spell fails, you may try again in 20 seconds.")) {
				castWaitTime = random(20000, 22500);
			}
		}
	}

}
