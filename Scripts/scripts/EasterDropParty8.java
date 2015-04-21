package scripts;

import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.Script;

public class EasterDropParty8 extends Script {


	
	public EasterDropParty8(Extension e) {
		super(e);

	}
	
	@Override
	public long run() {
		//kite shield 1647
		//dragon ammy 1749
		//ruby 1557
		int[] item = this.getGroundItemById(
				407, 406, 405, 404, 403, 402, 401, 400, 593, 594, 795, 597, 1278, 575, 576, 577, 578, 579, 580, 581, 971,1749, 1156, 1557, 677, 81, 1357
				);
		if(item[0] != -1) {
			this.takeItem(item);
			return this.random(450, 850);
		}
		return 1;
	}

}
