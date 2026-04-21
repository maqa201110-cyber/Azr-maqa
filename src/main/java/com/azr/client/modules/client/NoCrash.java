package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class NoCrash extends Module {
    public NoCrash() { super("NoCrash", "Catch and suppress non-fatal client errors (best-effort)", 0, ModuleCategory.CLIENT); }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(NoCrash.class);
        return m != null && m.isEnabled();
    }
}
