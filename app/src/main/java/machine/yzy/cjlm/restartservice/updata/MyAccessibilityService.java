package machine.yzy.cjlm.restartservice.updata;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xpp on 2018/3/16.
 */

public class MyAccessibilityService extends AccessibilityService {

    Map<Integer, Boolean> handledMap = new HashMap<>();

    public MyAccessibilityService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            int eventType = event.getEventType();
            Log.d("xpp","eventType>>"+eventType);
            if (eventType== AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                    eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (handledMap.get(event.getWindowId()) == null) {
                    boolean handled = iterateNodesAndHandle(nodeInfo);
                    if (handled) {
                        handledMap.put(event.getWindowId(), true);
                    }
                }else{
                    //Log.d("xpp","handledMap.get(event.getWindowId())>>"+handledMap.get(event.getWindowId()));
                }
            }else{
                //Log.d("xpp","eventType2>>"+eventType);
            }
        }
    }

    private boolean iterateNodesAndHandle(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            int childCount = nodeInfo.getChildCount();
            Log.d("xpp","childCount>>"+childCount);
            if ("android.widget.Button".equals(nodeInfo.getClassName())) {
                String nodeContent = nodeInfo.getText().toString();
                Log.d("xpp", "content is " + nodeContent);
                if ("安装".equals(nodeContent)) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }else if("完成".equals(nodeContent)){
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            } else if ("android.widget.ScrollView".equals(nodeInfo.getClassName())) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (iterateNodesAndHandle(childNodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {
    }
}
