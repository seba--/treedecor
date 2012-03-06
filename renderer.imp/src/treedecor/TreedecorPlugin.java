package treedecor;

import org.eclipse.imp.runtime.PluginBase;
import org.osgi.framework.BundleContext;

public class TreedecorPlugin extends PluginBase {

	public static final String kPluginID = "renderer.imp";
	public static final String kLanguageID = "treedecor";

	/**
	 * The unique instance of this plugin class
	 */
	protected static TreedecorPlugin sPlugin;

	public static TreedecorPlugin getInstance() {
		if (sPlugin == null)
			new TreedecorPlugin();
		return sPlugin;
	}

	public TreedecorPlugin() {
		super();
		sPlugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public String getID() {
		return kPluginID;
	}

	@Override
	public String getLanguageID() {
		return kLanguageID;
	}
}
