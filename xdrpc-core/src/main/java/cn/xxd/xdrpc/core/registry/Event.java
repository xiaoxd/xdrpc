package cn.xxd.xdrpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author xiaoxd
 * @create 2024/4/1
 **/
@Data
@AllArgsConstructor
public class Event {
    List<String> nodes;
}
