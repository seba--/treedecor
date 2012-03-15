package treedecorations;

import org.eclipse.imp.runtime.PluginBase;
import org.osgi.framework.BundleContext;

public class TreedecorationsPlugin extends PluginBase {

	public static final String kPluginID = "treedecorationsrenderer";
	public static final String kLanguageID = "Treedecorations";

	/**
	 * The unique instance of this plugin class
	 */
	protected static TreedecorationsPlugin sPlugin;

	public static TreedecorationsPlugin getInstance() {
		if (sPlugin == null)
			new TreedecorationsPlugin();
		return sPlugin;
	}

	public TreedecorationsPlugin() {
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
