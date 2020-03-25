package requesthandlers;

import response.StandardResponse;

public interface RequestHandler<T> {
    StandardResponse handle(T command,int port);
}
