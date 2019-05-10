package com.flyang.api.router;


import com.flyang.api.router.matcher.AbsExplicitMatcher;
import com.flyang.api.router.matcher.AbsImplicitMatcher;
import com.flyang.api.router.matcher.AbsMatcher;
import com.flyang.api.router.matcher.BrowserMatcher;
import com.flyang.api.router.matcher.DirectMatcher;
import com.flyang.api.router.matcher.ImplicitMatcher;
import com.flyang.api.router.matcher.SchemeMatcher;
import com.flyang.basic.log.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author caoyangfei
 * @ClassName MatcherRegistry
 * @date 2019/4/27
 * ------------- Description -------------
 * 路由匹配管理类
 */
public final class MatcherRegistry {

    private static final List<AbsMatcher> ALL = new ArrayList<>();
    private static final List<AbsExplicitMatcher> explicitMatcher = new ArrayList<>();
    private static final List<AbsImplicitMatcher> implicitMatcher = new ArrayList<>();

    static {
        ALL.add(new DirectMatcher(0x1000));
        ALL.add(new SchemeMatcher(0x0100));
        ALL.add(new ImplicitMatcher(0x0010));
        ALL.add(new BrowserMatcher(0x0000));
        Collections.sort(ALL);
        classifyMatcher();
    }

    public static void register(AbsMatcher matcher) {
        if (matcher instanceof AbsExplicitMatcher || matcher instanceof AbsImplicitMatcher) {
            ALL.add(matcher);
            Collections.sort(ALL);
            classifyMatcher();
        } else {
            LogUtils.e(String.format("%s must be a subclass of AbsExplicitMatcher or AbsImplicitMatcher",
                    matcher.getClass().getSimpleName()));
        }
    }

    public static List<AbsMatcher> getMatcher() {
        return ALL;
    }

    /**
     * 显示路由(跳转到有注解的activit or fragment)
     *
     * @return
     */
    public static List<AbsExplicitMatcher> getExplicitMatcher() {
        return explicitMatcher;
    }

    /**
     * 隐示路由(跳转到没有注解的activity)
     * example:
     * Browser,短信，电话等第三方
     *
     * @return
     */
    public static List<AbsImplicitMatcher> getImplicitMatcher() {
        return implicitMatcher;
    }

    public static void clear() {
        ALL.clear();
        explicitMatcher.clear();
        implicitMatcher.clear();
    }

    private static void classifyMatcher() {
        explicitMatcher.clear();
        implicitMatcher.clear();
        for (AbsMatcher absMatcher : ALL) {
            if (absMatcher instanceof AbsExplicitMatcher) {
                explicitMatcher.add((AbsExplicitMatcher) absMatcher);
            } else if (absMatcher instanceof AbsImplicitMatcher) {
                implicitMatcher.add((AbsImplicitMatcher) absMatcher);
            }
        }
    }
}
