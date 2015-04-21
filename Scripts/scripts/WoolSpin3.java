package scripts;

import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.Script;

public final class WoolSpin3 extends Script {

    private static final int WOOL = 145;
    private static final int WOOL_BALL = 207;

    public WoolSpin3(Extension ex) {
        super(ex);
    }

    @Override
    public long run() {
    	if(getInventoryCount(WOOL) > 0) {
    		if(inBank()) {
		    	closeBank();
		    	return random(350, 750);
    		}
    		int[] wheel = this.getObjectById(121);
    		if(wheel[0] != -1 && distanceTo(wheel[1], wheel[2]) < 4) {
    			this.useItemOnObject(WOOL,  121);
    			return random(700, 1250) * Math.max(1, this.getCurrentStatLevel(12) / 10);
    		}
    	}
    	if(inArea(280, 564, 286, 573) && getInventoryCount(WOOL) == 0) {
    		if(inBank()) {
    			if(this.getInventoryCount(WOOL_BALL) > 0) {
    				this.deposit(WOOL_BALL, getInventoryCount(WOOL_BALL));
    				return random(750, 1250);
    			}
    			else if(getInventoryCount() < 30) {
    				this.withdraw(WOOL, Math.min(30,  this.getBankCount(WOOL)));
    				System.out.println("wool count: " + getBankCount(WOOL));
    				return random(750, 1250);
    			}
    		} else {
    			if(this.isQuestionMenu()) {
    				this.answer(0);
    				return random(2000, 2500);
    			}
    			
    			int[] banker = this.getNpcById(BANKERS);
    			if(banker[0] != -1) {
    				this.talkToNpc(banker[0]);
    				return random(3000, 4000);
    			}
    		}
    	}
    	return 1000;
    }
}