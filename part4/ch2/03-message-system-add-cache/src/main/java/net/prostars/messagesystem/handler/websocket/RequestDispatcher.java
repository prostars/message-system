package net.prostars.messagesystem.handler.websocket;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import net.prostars.messagesystem.dto.websocket.inbound.BaseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class RequestDispatcher {

  private static final Logger log = LoggerFactory.getLogger(RequestDispatcher.class);

  private final Map<Class<? extends BaseRequest>, BaseRequestHandler<? extends BaseRequest>>
      handlerMap = new HashMap<>();
  private final ListableBeanFactory listableBeanFactory;

  public RequestDispatcher(ListableBeanFactory listableBeanFactory) {
    this.listableBeanFactory = listableBeanFactory;
  }

  public <T extends BaseRequest> void dispatchRequest(
      WebSocketSession webSocketSession, T request) {
    BaseRequestHandler<T> handler = (BaseRequestHandler<T>) handlerMap.get(request.getClass());
    if (handler != null) {
      handler.handleRequest(webSocketSession, request);
      return;
    }
    log.error("Handler not found for request type: {}", request.getClass().getSimpleName());
  }

  @PostConstruct
  private void prepareRequestHandlerMapping() {
    Map<String, BaseRequestHandler> beanHandlers =
        listableBeanFactory.getBeansOfType(BaseRequestHandler.class);
    for (BaseRequestHandler handler : beanHandlers.values()) {
      Class<? extends BaseRequest> requestClass = extractRequestClass(handler);
      if (requestClass != null) {
        handlerMap.put(requestClass, handler);
      }
    }
  }

  private Class<? extends BaseRequest> extractRequestClass(BaseRequestHandler handler) {
    for (Type type : handler.getClass().getGenericInterfaces()) {
      if (type instanceof ParameterizedType parameterizedType
          && parameterizedType.getRawType().equals(BaseRequestHandler.class)) {
        return (Class<? extends BaseRequest>) parameterizedType.getActualTypeArguments()[0];
      }
    }
    return null;
  }
}
