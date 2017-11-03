package doext.implement;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import core.DoServiceContainer;
import core.helper.DoIOHelper;
import core.helper.DoJsonHelper;
import core.interfaces.DoIScriptEngine;
import core.interfaces.DoISourceFS;
import core.object.DoInvokeResult;
import core.object.DoSingletonModule;
import doext.define.do_Storage_IMethod;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现do_Storage_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.getUniqueKey());
 */
public class do_Storage_Model extends DoSingletonModule implements do_Storage_IMethod {

	public do_Storage_Model() throws Exception {
		super();
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		// ...do something
		if ("dirExist".equals(_methodName)) {
			this.dirExist(_dictParas, _scriptEngine, _invokeResult);
			return true;
		} else if ("fileExist".equals(_methodName)) {
			this.fileExist(_dictParas, _scriptEngine, _invokeResult);
			return true;
		} else if ("readFileSync".equals(_methodName)) {
			this.readFileSync(_dictParas, _scriptEngine, _invokeResult);
			return true;
		} else if ("getFileSize".equals(_methodName)) {
			this.getFileSize(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		// ...do something
		DoInvokeResult _invokeResult = new DoInvokeResult(this.getUniqueKey());
		if ("getFiles".equals(_methodName)) {
			this.getFiles(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("getDirs".equals(_methodName)) {
			this.getDirs(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("deleteDir".equals(_methodName)) {
			this.deleteDir(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("deleteFile".equals(_methodName)) {
			this.deleteFile(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("readFile".equals(_methodName)) {
			this.readFile(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("writeFile".equals(_methodName)) {
			this.writeFile(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("zip".equals(_methodName)) {
			this.zip(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("unzip".equals(_methodName)) {
			this.unzip(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("copy".equals(_methodName)) {
			this.copy(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("zipFiles".equals(_methodName)) {
			this.zipFiles(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("copyFile".equals(_methodName)) {
			this.copyFile(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
		}
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 拷贝文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void copy(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			JSONArray _sources = DoJsonHelper.getJSONArray(_dictParas, "source");
			if (_sources == null || _sources.length() <= 0) {
				throw new Exception("source不能为空!");
			}
			// 检查source里面是否包含了不合法目录(不支持source://开头的目录)
			if (!DoIOHelper.checkFilePathValidate(_sources)) {
				throw new Exception("source参数只支持" + DoISourceFS.DATA_PREFIX + " 打头!");
			}

			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			_target = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _target);
			for (int i = 0; i < _sources.length(); i++) {
				String _fullPath = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _sources.getString(i));
				DoIOHelper.copyFileOrDirectory(_fullPath, _target);
			}
			_invokeResult.setResultBoolean(true);
		} catch (Exception e) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_Storage_Model copy /t", e);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 拷贝文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void copyFile(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _source = DoJsonHelper.getString(_dictParas, "source", "");
			if (TextUtils.isEmpty(_source)) {
				throw new Exception("source不能为空!");
			}
			if (!_source.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("source参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			String _sourceFullPath = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _source);
			if (!DoIOHelper.existFileOrDirectory(_sourceFullPath)) {
				throw new Exception(_source + " 文件或者目录不存在!");
			}

			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}

			_target = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _target);
			DoIOHelper.copyFileByBybeBuffer(_sourceFullPath, _target);
			_invokeResult.setResultBoolean(true);
		} catch (Exception e) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_Storage_Model copyFile /r/n", e);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 删除目录；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void deleteDir(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			String _dirFullPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
			DoIOHelper.deleteDirectory(_dirFullPath);
			_invokeResult.setResultBoolean(true);
		} catch (Exception ex) {
			_invokeResult.setResultBoolean(false);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 删除文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void deleteFile(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			String _dirFullPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
			DoIOHelper.deleteFile(_dirFullPath);
			_invokeResult.setResultBoolean(true);
		} catch (Exception ex) {
			_invokeResult.setResultBoolean(false);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 判断目录是否存在；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void dirExist(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _path = DoJsonHelper.getString(_dictParas, "path", "");
		if (TextUtils.isEmpty(_path)) {
			throw new Exception("path不能为空!");
		}
		if (!_path.startsWith(DoISourceFS.DATA_PREFIX)) {
			throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
		}
		String _dirFullPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
		_invokeResult.setResultBoolean(DoIOHelper.existDirectory(_dirFullPath));
	}

	/**
	 * 判断文件是否存在；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void fileExist(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _path = DoJsonHelper.getString(_dictParas, "path", "");
		if (TextUtils.isEmpty(_path)) {
			throw new Exception("path不能为空!");
		}
		if (!_path.startsWith(DoISourceFS.DATA_PREFIX)) {
			throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
		}
		String _fileFullPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
		_invokeResult.setResultBoolean(DoIOHelper.existFile(_fileFullPath));
	}

	@Override
	public void getFileSize(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _path = DoJsonHelper.getString(_dictParas, "path", "");
		if (TextUtils.isEmpty(_path)) {
			throw new Exception("path不能为空!");
		}
		if (!_path.startsWith(DoISourceFS.DATA_PREFIX)) {
			throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
		}
		String _fileFullPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
		File _mFile = new File(_fileFullPath);
		if (_mFile.isDirectory()) {
			_invokeResult.setResultText("0");
			throw new Exception(_path + "是一个目录!");
		}
		_invokeResult.setResultText(_mFile.length() + "");
	}

	/**
	 * 获取目录列表；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void getDirs(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			String _dirFullPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
			List<String> _listAppDirs = DoIOHelper.getDirectories(_dirFullPath);
			JSONArray _tempArray = new JSONArray();
			for (String _dirPaht : _listAppDirs) {
				_tempArray.put(_dirPaht);
			}
			_invokeResult.setResultArray(_tempArray);
		} catch (Exception ex) {
			_invokeResult.setException(ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 获取文件列表；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void getFiles(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			String _dirFullPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
			List<String> _listAppFiles = DoIOHelper.getFiles(_dirFullPath);
			JSONArray _tempArray = new JSONArray();
			for (String _dirPaht : _listAppFiles) {
				_tempArray.put(_dirPaht);
			}
			_invokeResult.setResultArray(_tempArray);
		} catch (Exception ex) {
			_invokeResult.setException(ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 读取文件内容；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void readFile(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		String _content = "";
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			String _fileFullName = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
			if (!DoIOHelper.existFile(_fileFullName)) {
				throw new Exception(_path + "文件不存在！");
			}
			String _encoding = DoJsonHelper.getString(_dictParas, "encoding", "utf-8");
			boolean _isSecurity = DoJsonHelper.getBoolean(_dictParas, "isSecurity", false);
			byte[] _bytes = null;
			if (DoServiceContainer.getVersionType() != 1 && (_isSecurity || _path.startsWith(DoISourceFS.DATA_SECURITY_PREFIX))) {
				_bytes = DoIOHelper.decodeFile(_fileFullName, DoServiceContainer.getDataKey());
			} else {
				_bytes = DoIOHelper.readAllBytes(_fileFullName);

			}
			if (_bytes != null) {
				if ("gbk".equalsIgnoreCase(_encoding)) {
					_content = new String(_bytes, Charset.forName("gbk"));
				} else {
					_content = DoIOHelper.getUTF8String(_bytes);
				}
			}

		} catch (Exception ex) {
			_invokeResult.setException(ex);
		} finally {
			_invokeResult.setResultText(_content);
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 读取文件内容；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void readFileSync(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _content = "";
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			String _fileFullName = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
			if (!DoIOHelper.existFile(_fileFullName)) {
				throw new Exception(_path + "文件不存在！");
			}
			String _encoding = DoJsonHelper.getString(_dictParas, "encoding", "utf-8");
			boolean _isSecurity = DoJsonHelper.getBoolean(_dictParas, "isSecurity", false);
			byte[] _bytes = null;
			if (DoServiceContainer.getVersionType() != 1 && (_isSecurity || _path.startsWith(DoISourceFS.DATA_SECURITY_PREFIX))) {
				_bytes = DoIOHelper.decodeFile(_fileFullName, DoServiceContainer.getDataKey());
			} else {
				_bytes = DoIOHelper.readAllBytes(_fileFullName);

			}
			if (_bytes != null) {
				if ("gbk".equalsIgnoreCase(_encoding)) {
					_content = new String(_bytes, Charset.forName("gbk"));
				} else {
					_content = DoIOHelper.getUTF8String(_bytes);
				}
			}
		} catch (Exception ex) {
			_invokeResult.setException(ex);
		} finally {
			_invokeResult.setResultText(_content);
		}
	}

	/**
	 * 解压缩文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void unzip(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _source = DoJsonHelper.getString(_dictParas, "source", "");
			if (TextUtils.isEmpty(_source)) {
				throw new Exception("source不能为空!");
			}
			if (!_source.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("source参数只支持" + DoISourceFS.DATA_PREFIX + " 打头!");
			}

			String _sourceFullPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_source);
			if (!DoIOHelper.existFile(_sourceFullPath)) {
				throw new Exception(_source + " 文件不存在!");
			}

			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持" + DoISourceFS.DATA_PREFIX + " 打头!");
			}

			_target = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_target);
			DoIOHelper.unZipFolder(_sourceFullPath, _target);
			_invokeResult.setResultBoolean(true);
		} catch (Exception ex) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_Storage_Model unzip /t", ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 写文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void writeFile(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _fileName = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_fileName)) {
				throw new Exception("path不能为空!");
			}
			if (!_fileName.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			String _fileData = DoJsonHelper.getString(_dictParas, "data", "");
			String _fileFullName = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_fileName);
			String _encoding = DoJsonHelper.getString(_dictParas, "encoding", "utf-8");
			boolean _isSecurity = DoJsonHelper.getBoolean(_dictParas, "isSecurity", false);
			boolean _isAppend = DoJsonHelper.getBoolean(_dictParas, "isAppend", false);
			if (DoServiceContainer.getVersionType() != 1 && (_isSecurity || _fileName.startsWith(DoISourceFS.DATA_SECURITY_PREFIX))) {
				DoIOHelper.encryptFile(_fileFullName, _fileData, DoServiceContainer.getDataKey(), _encoding);
			} else {
				DoIOHelper.writeAllText(_fileFullName, _fileData, _encoding, _isAppend);
			}
			_invokeResult.setResultBoolean(true);
		} catch (Exception ex) {
			_invokeResult.setResultBoolean(false);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 压缩文件或目录；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void zip(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _source = DoJsonHelper.getString(_dictParas, "source", "");
			if (TextUtils.isEmpty(_source)) {
				throw new Exception("source不能为空!");
			}
			if (!_source.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("source参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			String _sourceFullPath = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _source);
			if (!DoIOHelper.existFileOrDirectory(_sourceFullPath)) {
				throw new Exception(_source + " 文件或者目录不存在!");
			}

			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}

			_target = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_target);
			if (!DoIOHelper.existFile(_target)) {
				DoIOHelper.createFile(_target);
			}

			File _sourceFile = new File(_sourceFullPath);
			if (_sourceFile.isDirectory()) {
				DoIOHelper.zipFolder(_sourceFile.listFiles(), _target);
			} else {
				DoIOHelper.zipFile(_sourceFile, _target);
			}
			_invokeResult.setResultBoolean(true);
		} catch (Exception ex) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_Storage_Model zip \t", ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 压缩多个文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void zipFiles(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			JSONArray _sources = DoJsonHelper.getJSONArray(_dictParas, "source");
			if (_sources == null || _sources.length() == 0) {
				throw new Exception("source不能为空!");
			}
			// 检查source里面是否包含了不合法目录(不支持source://开头的目录)
			if (!DoIOHelper.checkFilePathValidate(_sources)) {
				throw new Exception("source参数只支持" + DoISourceFS.DATA_PREFIX + " 打头!");
			}
			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			_target = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_target);

			File _targetFile = new File(_target);
			File _parentFile = _targetFile.getParentFile();
			if (!_parentFile.exists()) {
				_parentFile.mkdirs();
			}
			// 先copy 到一个temp 目录下面
			File _tempFile = new File(_parentFile, System.currentTimeMillis() + "");
			_tempFile.mkdir();
			for (int i = 0; i < _sources.length(); i++) {
				String _fullPath = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _sources.getString(i));
				if (DoIOHelper.existFile(_fullPath)) {
					DoIOHelper.copyFileOrDirectory(_fullPath, _tempFile.getAbsolutePath());
				}
			}
			DoIOHelper.zipFolder(_tempFile.listFiles(), _target);
			// 删除临时目录
			DoIOHelper.deleteDirectory(_tempFile.getAbsolutePath());
			_invokeResult.setResultBoolean(true);
		} catch (Exception ex) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_Storage_Model zipFiles /t", ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

}