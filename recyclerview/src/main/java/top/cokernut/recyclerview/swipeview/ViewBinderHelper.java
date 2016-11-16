package top.cokernut.recyclerview.swipeview;

import android.os.Bundle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ViewBinderHelper提供了一个快速和简单的方案来恢复打开/关闭状态，可以用于RecyclerView, ListView, GridView
 */
public class ViewBinderHelper {
    private static final String BUNDLE_MAP_KEY = "ViewBinderHelper_Bundle_Map_Key";

    private Map<String, Integer> mapStates = Collections.synchronizedMap(new HashMap<String, Integer>());
    private Map<String, SwipeRevealLayout> mapLayouts = Collections.synchronizedMap(new HashMap<String, SwipeRevealLayout>());

    private volatile boolean openOnlyOne = false;
    private final Object stateChangeLock = new Object();

    /**
     * 有助于保存和恢复swipeLayout的打开/关闭状态
     *
     * @param swipeLayout swipeLayout view.
     * @param id 这是唯一性的，能够标记当前数据对象
     */
    public void bind(final SwipeRevealLayout swipeLayout, final String id) {
        mapLayouts.values().remove(swipeLayout);
        mapLayouts.put(id, swipeLayout);

        swipeLayout.abort();
        swipeLayout.setDragStateChangeListener(new SwipeRevealLayout.DragStateChangeListener() {
            @Override
            public void onDragStateChanged(int state) {
                mapStates.put(id, state);

                if (openOnlyOne) {
                    closeOthers(id, swipeLayout);
                }
            }
        });

        // 第一次绑定。
        if (!mapStates.containsKey(id)) {
            mapStates.put(id, SwipeRevealLayout.STATE_CLOSE);
            swipeLayout.close(false);
        }

        // 不是第一次,然后关闭或打开取决于当前状态。
        else {
            int state = mapStates.get(id);

            if (state == SwipeRevealLayout.STATE_CLOSE || state == SwipeRevealLayout.STATE_CLOSING ||
                    state == SwipeRevealLayout.STATE_DRAGGING) {
                swipeLayout.close(false);
            } else {
                swipeLayout.open(false);
            }
        }
    }

    /**
     * 如果状态发生改变，用于保存打开/关闭状态
     */
    public void saveStates(Bundle outState) {
        if (outState == null)
            return;

        Bundle statesBundle = new Bundle();
        for (Map.Entry<String, Integer> entry : mapStates.entrySet()) {
            statesBundle.putInt(entry.getKey(), entry.getValue());
        }

        outState.putBundle(BUNDLE_MAP_KEY, statesBundle);
    }


    /**
     * 恢复打开/关闭状态
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void restoreStates(Bundle inState) {
        if (inState == null)
            return;

        if (inState.containsKey(BUNDLE_MAP_KEY)) {
            HashMap<String, Integer> restoredMap = new HashMap<>();

            Bundle statesBundle = inState.getBundle(BUNDLE_MAP_KEY);
            Set<String> keySet = statesBundle.keySet();

            if (keySet != null) {
                for (String key : keySet) {
                    restoredMap.put(key, statesBundle.getInt(key));
                }
            }

            mapStates = restoredMap;
        }
    }

    /**
     * @param openOnlyOne 如果设置为true,那么一次只能打开一行。
     */
    public void setOpenOnlyOne(boolean openOnlyOne) {
        this.openOnlyOne = openOnlyOne;
    }

    /**
     * 打开一个指定的布局。
     * @param id id标识的数据对象
     */
    public void openLayout(final String id) {
        synchronized (stateChangeLock) {
            mapStates.put(id, SwipeRevealLayout.STATE_OPEN);

            if (mapLayouts.containsKey(id)) {
                final SwipeRevealLayout layout = mapLayouts.get(id);
                layout.open(true);
            }
            else if (openOnlyOne) {
                closeOthers(id, mapLayouts.get(id));
            }
        }
    }

    /**
     * 关闭一个指定的布局。
     * @param id id标识的数据对象
     */
    public void closeLayout(final String id) {
        synchronized (stateChangeLock) {
            mapStates.put(id, SwipeRevealLayout.STATE_CLOSE);

            if (mapLayouts.containsKey(id)) {
                final SwipeRevealLayout layout = mapLayouts.get(id);
                layout.close(true);
            }
        }
    }

    /**
     * 关闭其他布局
     * @param id 排除的id标识的数据对象
     * @param swipeLayout 排除的swipeLayout.
     */
    private void closeOthers(String id, SwipeRevealLayout swipeLayout) {
        synchronized (stateChangeLock) {
            // 关闭打开的其他行
            if (getOpenCount() > 1) {
                for (Map.Entry<String, Integer> entry : mapStates.entrySet()) {
                    if (!entry.getKey().equals(id)) {
                        entry.setValue(SwipeRevealLayout.STATE_CLOSE);
                    }
                }

                for (SwipeRevealLayout layout : mapLayouts.values()) {
                    if (layout != swipeLayout) {
                        layout.close(true);
                    }
                }
            }
        }
    }

    private int getOpenCount() {
        int total = 0;

        for (int state : mapStates.values()) {
            if (state == SwipeRevealLayout.STATE_OPEN || state == SwipeRevealLayout.STATE_OPENING) {
                total++;
            }
        }

        return total;
    }
}
