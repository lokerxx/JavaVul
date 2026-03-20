from .log4j import requests_config as log4j_requests
from .fastjson import requests_config as fastjson_requests
from .shiro import requests_config as shiro_requests
from .actuator import requests_config as actuator_requests
from .druid import requests_config as druid_requests
from .data_access import requests_config as data_access_requests
from .integration import requests_config as integration_requests
from .base_vul import requests_config as base_vul_requests
from .logic_vul import requests_config as logic_vul_requests
from .struts import requests_config as struts_requests
from .collections import requests_config as collections_requests


requests_config = {}
for module in (
    log4j_requests,
    fastjson_requests,
    shiro_requests,
    actuator_requests,
    druid_requests,
    data_access_requests,
    integration_requests,
    base_vul_requests,
    logic_vul_requests,
    struts_requests,
    collections_requests,
):
    requests_config.update(module)
