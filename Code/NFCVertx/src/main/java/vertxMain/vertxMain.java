package vertxMain;

import database.DatabaseVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import telegram.telegramMain;

public class vertxMain extends AbstractVerticle{

	
	@Override
	public void start(Promise<Void> startPromise) {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		
		//vertx.deployVerticle(telegramMain.class.getName());
		vertx.deployVerticle(DatabaseVerticle.class.getName());
	}
}