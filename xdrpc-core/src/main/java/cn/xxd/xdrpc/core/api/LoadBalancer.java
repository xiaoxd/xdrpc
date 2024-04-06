package cn.xxd.xdrpc.core.api;

import java.util.List;


/**
 * @author xiaoxd
 * @create 2024-03-30
 * @summary ���ؾ��⣬Ȩ�أ��������ѯ��һ���Թ�ϣ����С����������С��Ӧʱ�䣬�Զ���
 * LoadBalancer
 */
public interface LoadBalancer<T> {
    LoadBalancer Default = p -> (p == null || p.isEmpty()) ? null : p.get(0);

    T choose(List<T> providers);
}
