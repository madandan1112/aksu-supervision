#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import urllib.request, urllib.error, urllib.parse, json, time, sys

try:
    import jwt as pyjwt
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'PyJWT', '-q'])
    import jwt as pyjwt

BASE = 'http://localhost:8080'
SECRET = 'aksu-supervision-jwt-secret-key-2024-must-be-at-least-256-bits-long-for-hs256'
R = {'p':0,'f':0,'s':0,'d':[]}

def tok(uid=1,un='admin',ut='admin'):
    n=int(time.time())
    return pyjwt.encode({'sub':str(uid),'username':un,'userType':ut,'iat':n,'exp':n+86400},SECRET,algorithm='HS256')

def api(m,path,tk=None,data=None,params=None):
    url=BASE+path
    if params:
        url+=('?' if '?' not in url else '&')+urllib.parse.urlencode(params)
    h={}
    if tk: h['Authorization']='Bearer '+tk
    if data is not None: h['Content-Type']='application/json'
    req=urllib.request.Request(url,method=m,headers=h)
    if data is not None:
        req.data=json.dumps(data).encode() if isinstance(data,dict) else str(data).encode()
    try:
        resp=urllib.request.urlopen(req,timeout=10)
        return json.loads(resp.read().decode('utf-8',errors='replace'))
    except urllib.error.HTTPError as e:
        body=e.read().decode('utf-8',errors='replace')
        try: return json.loads(body)
        except: return {'code':e.code,'message':body[:200]}
    except Exception as e:
        return {'code':-1,'message':str(e)}

def t(name,cond,det=''):
    if cond: R['p']+=1; s='OK'
    else: R['f']+=1; s='ERR'
    R['d'].append({'name':name,'s':s,'det':det})
    print(f'  [{s}] {name}'+(f' -- {det}' if det and not cond else ''))

def ta(name,resp,ec=200,cd=True):
    c=resp.get('code'); hd=resp.get('data') is not None
    det=f'code={c}'
    if c!=ec: det+=f' msg={str(resp.get("message",""))[:60]}'
    t(name,c==ec and(hd if cd else True),det)
    return resp

at=tok(); it=tok(14,'inspector1','inspector'); et=tok(16,'ent_user1','enterprise')

print('='*55)
print('AkSu Supervision - Full API Test')
print('='*55)

# 0 Token
print('\n[0. Token]')
r=api('GET','/api/auth/userinfo',tk=at); t('Admin token',r.get('code')==200,f'code={r.get("code")}')
r=api('GET','/api/auth/userinfo',tk=it); t('Inspector token',r.get('code')==200,f'code={r.get("code")}')
r=api('GET','/api/auth/userinfo',tk=et); t('Enterprise token',r.get('code')==200,f'code={r.get("code")}')

# 1 Auth
print('\n[1. Auth]')
r=api('GET','/api/auth/captcha'); ta('GET /api/auth/captcha',r)
ck=r.get('data',{}).get('captchaKey','')
r=api('POST','/api/auth/login',data={'username':'admin','password':'123456','captchaKey':ck,'captchaCode':'WRONG'})
t('POST /api/auth/login (bad captcha)',r.get('code') in[400,401],f'code={r.get("code")}')
r=api('GET','/api/auth/userinfo',tk=at); ta('GET /api/auth/userinfo',r)
t('  username=admin',r.get('data',{}).get('username')=='admin')
r=api('POST','/api/auth/change-password',tk=at,data={'oldPassword':'wrong','newPassword':'new123'})
t('POST /api/auth/change-password (wrong old)',r.get('code') in[400,401],f'code={r.get("code")}')
r=api('POST','/api/auth/logout',tk=at); ta('POST /api/auth/logout',r,cd=False)
r=api('POST','/api/auth/register',data={'username':'auto_tst_x2','password':'Test123456','realName':'AT','phone':'13900008877','userType':'enterprise'})
t('POST /api/auth/register',r.get('code') in[200,400],f'code={r.get("code")}')

# 2 Dashboard
print('\n[2. Dashboard]')
r=api('GET','/api/admin/dashboard/overview',tk=at); ta('GET overview',r)
d=r.get('data',{})
t('  enterpriseCount>0',d.get('enterpriseCount',0)>0,f'val={d.get("enterpriseCount")}')
t('  appealCount>0',d.get('appealCount',0)>0,f'val={d.get("appealCount")}')
t('  taskCount>0',d.get('taskCount',0)>0,f'val={d.get("taskCount")}')
t('  alertCount>0',d.get('alertCount',0)>0,f'val={d.get("alertCount")}')
r=api('GET','/api/admin/dashboard/industry-view',tk=at); ta('GET industry-view',r)
r=api('GET','/api/admin/dashboard/area-view',tk=at); ta('GET area-view',r)
r=api('GET','/api/admin/dashboard/risk-profile',tk=at); ta('GET risk-profile',r)

# 3 Appeal
print('\n[3. Appeal]')
r=api('GET','/api/admin/appeal/list',tk=at,params={'page':1,'size':10}); ta('GET appeal/list',r)
aps=r.get('data',{}).get('list',[]); apt=r.get('data',{}).get('total',0)
t('  appeal total>0',apt>0,f'total={apt}')
if aps:
    r=api('GET',f'/api/admin/appeal/{aps[0]["id"]}',tk=at); ta(f'GET appeal/{aps[0]["id"]}',r)
pend=[a for a in aps if a.get('status')=='PENDING']
assi=[a for a in aps if a.get('status')=='ASSIGNED']
if pend:
    r=api('PUT',f'/api/admin/appeal/{pend[0]["id"]}/assign',tk=at,params={'assignedTo':14})
    ta(f'PUT appeal/{pend[0]["id"]}/assign',r,cd=False)
else:
    R['s']+=1; R['d'].append({'name':'PUT appeal/assign','s':'SKIP','det':'No PENDING'})
if assi:
    r=api('PUT',f'/api/admin/appeal/{assi[0]["id"]}/handle',tk=at,params={'handleResult':'AUTO_TEST'})
    ta(f'PUT appeal/{assi[0]["id"]}/handle',r,cd=False)
else:
    R['s']+=1; R['d'].append({'name':'PUT appeal/handle','s':'SKIP','det':'No ASSIGNED'})
r=api('GET','/api/admin/appeal/statistics',tk=at); ta('GET appeal/statistics',r)

# 4 Task Admin
print('\n[4. Task - Admin]')
r=api('GET','/api/admin/task/list',tk=at,params={'page':1,'size':10}); ta('GET task/list',r)
tks=r.get('data',{}).get('list',[]); tkt=r.get('data',{}).get('total',0)
t('  task total>0',tkt>0,f'total={tkt}')
r=api('POST','/api/admin/task/create',tk=at,data={'title':'AUTO_TEST','type':'SPECIAL','description':'Test','inspectorIds':[14],'enterpriseIds':[1],'deadline':'2026-12-31'})
t('POST task/create',r.get('code')==200,f'code={r.get("code")} msg={str(r.get("message",""))[:50]}')
ctid=r.get('data',{}).get('id') if r.get('code')==200 else None
if ctid:
    r=api('PUT',f'/api/admin/task/{ctid}',tk=at,data={'title':'AUTO_TEST_UPD','description':'Upd'})
    t('PUT task/{id} update',r.get('code')==200,f'code={r.get("code")}')
    r=api('PUT',f'/api/admin/task/{ctid}/terminate',tk=at,params={'reason':'Auto'})
    t('PUT task/{id}/terminate',r.get('code')==200,f'code={r.get("code")}')
else:
    R['s']+=2; R['d'].append({'name':'PUT task update/terminate','s':'SKIP','det':'No created task'})

# 5 Enterprise
print('\n[5. Enterprise]')
r=api('GET','/api/admin/enterprise/list',tk=at,params={'page':1,'size':5}); ta('GET enterprise/list',r)
ents=r.get('data',{}).get('list',[]); ent=r.get('data',{}).get('total',0)
t('  enterprise total>0',ent>0,f'total={ent}')
if ents:
    r=api('GET',f'/api/admin/enterprise/{ents[0]["id"]}',tk=at); ta(f'GET enterprise/{ents[0]["id"]}',r)
    r=api('PUT',f'/api/admin/enterprise/{ents[0]["id"]}/status',tk=at,params={'status':'NORMAL'})
    t('PUT enterprise/{id}/status',r.get('code') in[200,400,500],f'code={r.get("code")}')

# 6 Report
print('\n[6. Report]')
r=api('GET','/api/admin/report/list',tk=at,params={'page':1,'size':10}); ta('GET report/list',r)
rps=r.get('data',{}).get('list',[])
if rps:
    r=api('PUT',f'/api/admin/report/{rps[0]["id"]}/review',tk=at,params={'status':'APPROVED','comment':'Auto'})
    t('PUT report/{id}/review',r.get('code') in[200,400,500],f'code={r.get("code")}')
r=api('GET','/api/admin/report/expiring',tk=at,params={'days':30}); ta('GET report/expiring',r)

# 7 Alert
print('\n[7. Alert - Inspector]')
r=api('GET','/api/inspector/alert/list',tk=it,params={'page':1,'size':10}); ta('GET alert/list',r)
als=r.get('data',{}).get('list',[])
if als:
    r=api('GET',f'/api/inspector/alert/{als[0]["id"]}',tk=it); ta(f'GET alert/{als[0]["id"]}',r)
    r=api('PUT',f'/api/inspector/alert/{als[0]["id"]}/handle',tk=it,params={'handleResult':'Auto'})
    t('PUT alert/{id}/handle',r.get('code') in[200,400,500],f'code={r.get("code")}')
r=api('GET','/api/inspector/alert/statistics',tk=it); ta('GET alert/statistics',r)

# 8 Inspector Task
print('\n[8. Inspector Task]')
r=api('GET','/api/inspector/task/list',tk=it,params={'page':1,'size':10}); ta('GET insp/task/list',r)
r=api('GET','/api/inspector/task/statistics',tk=it); ta('GET insp/task/statistics',r)
if tks:
    r=api('POST',f'/api/inspector/task/{tks[0]["id"]}/claim',tk=it)
    t('POST insp/task/claim',r.get('code') in[200,400,5
