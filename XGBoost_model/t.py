import xgboost
from xgboost.libpath import find_lib_path

import os

print(find_lib_path())
print(os.path.join(os.path.dirname(xgboost.__file__), "VERSION"))