import { useEffect, useMemo, useState } from 'react'
/* eslint-disable react-hooks/exhaustive-deps */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const TOKEN_KEY = 'church_erp_access_token'

const emptyOrgForm = { code: '', name: '', type: '', parentId: '', active: true }
const emptyCodeForm = { groupCode: '', code: '', name: '', sortOrder: 0, description: '', active: true }
const emptyPeriodForm = { fiscalYear: new Date().getFullYear(), periodNo: 1, startDate: '', endDate: '', status: 'OPEN', active: true }
const emptyAccountForm = { accountCode: '', accountName: '', accountType: 'ASSET', parentId: '', active: true }

function App() {
  const [tab, setTab] = useState('org')
  const [loginId, setLoginId] = useState('admin')
  const [password, setPassword] = useState('password')
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY) || '')
  const [user, setUser] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const [orgKeyword, setOrgKeyword] = useState('')
  const [orgActive, setOrgActive] = useState('')
  const [orgItems, setOrgItems] = useState([])
  const [orgForm, setOrgForm] = useState(emptyOrgForm)
  const [orgEditId, setOrgEditId] = useState(null)

  const [codeKeyword, setCodeKeyword] = useState('')
  const [codeGroupFilter, setCodeGroupFilter] = useState('')
  const [codeActive, setCodeActive] = useState('')
  const [codeItems, setCodeItems] = useState([])
  const [codeForm, setCodeForm] = useState(emptyCodeForm)
  const [codeEditId, setCodeEditId] = useState(null)

  const [periodYearFilter, setPeriodYearFilter] = useState('')
  const [periodStatusFilter, setPeriodStatusFilter] = useState('')
  const [periodActiveFilter, setPeriodActiveFilter] = useState('')
  const [periodItems, setPeriodItems] = useState([])
  const [periodForm, setPeriodForm] = useState(emptyPeriodForm)
  const [periodEditId, setPeriodEditId] = useState(null)

  const [accountKeyword, setAccountKeyword] = useState('')
  const [accountTypeFilter, setAccountTypeFilter] = useState('')
  const [accountActiveFilter, setAccountActiveFilter] = useState('')
  const [accountItems, setAccountItems] = useState([])
  const [accountForm, setAccountForm] = useState(emptyAccountForm)
  const [accountEditId, setAccountEditId] = useState(null)

  const loggedIn = useMemo(() => token.length > 0 && user !== null, [token, user])

  useEffect(() => {
    if (!token) {
      setUser(null)
      return
    }

    let cancelled = false
    const bootstrap = async () => {
      setLoading(true)
      setError('')
      try {
        const meResponse = await fetch(`${API_BASE_URL}/api/auth/me`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        const meJson = await meResponse.json()
        if (!meResponse.ok || !meJson.success) {
          throw new Error(meJson.message || '인증 확인 실패')
        }
        if (cancelled) return
        setUser(meJson.data)

        await Promise.all([
          loadOrganizations(token),
          loadCommonCodes(token),
          loadFinancePeriods(token),
          loadFinanceAccounts(token),
        ])
      } catch (err) {
        if (!cancelled) {
          localStorage.removeItem(TOKEN_KEY)
          setToken('')
          setUser(null)
          setError(err.message)
        }
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    void bootstrap()
    return () => {
      cancelled = true
    }
  }, [token])

  async function api(path, options = {}, customToken = token) {
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) }
    if (customToken) headers.Authorization = `Bearer ${customToken}`

    const response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers })
    const json = await response.json()
    if (!response.ok || !json.success) {
      throw new Error(json.message || '요청 처리 중 오류가 발생했습니다.')
    }
    return json.data
  }

  async function onLogin(event) {
    event.preventDefault()
    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ loginId, password }),
      })
      const json = await response.json()
      if (!response.ok || !json.success) throw new Error(json.message || '로그인 실패')
      localStorage.setItem(TOKEN_KEY, json.data.accessToken)
      setToken(json.data.accessToken)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function onLogout() {
    await api('/api/auth/logout', { method: 'POST' }).catch(() => null)
    localStorage.removeItem(TOKEN_KEY)
    setToken('')
    setUser(null)
  }

  async function loadOrganizations(customToken = token) {
    const query = new URLSearchParams({ page: '0', size: '50' })
    if (orgKeyword.trim()) query.set('keyword', orgKeyword.trim())
    if (orgActive !== '') query.set('active', orgActive)
    const data = await api(`/api/organizations?${query.toString()}`, {}, customToken)
    setOrgItems(data.items)
  }

  async function loadCommonCodes(customToken = token) {
    const query = new URLSearchParams({ page: '0', size: '100' })
    if (codeKeyword.trim()) query.set('keyword', codeKeyword.trim())
    if (codeGroupFilter.trim()) query.set('groupCode', codeGroupFilter.trim())
    if (codeActive !== '') query.set('active', codeActive)
    const data = await api(`/api/common-codes?${query.toString()}`, {}, customToken)
    setCodeItems(data.items)
  }

  async function loadFinancePeriods(customToken = token) {
    const query = new URLSearchParams({ page: '0', size: '100' })
    if (periodYearFilter !== '') query.set('fiscalYear', periodYearFilter)
    if (periodStatusFilter !== '') query.set('status', periodStatusFilter)
    if (periodActiveFilter !== '') query.set('active', periodActiveFilter)
    const data = await api(`/api/finance/periods?${query.toString()}`, {}, customToken)
    setPeriodItems(data.items)
  }

  async function loadFinanceAccounts(customToken = token) {
    const query = new URLSearchParams({ page: '0', size: '100' })
    if (accountKeyword.trim()) query.set('keyword', accountKeyword.trim())
    if (accountTypeFilter !== '') query.set('accountType', accountTypeFilter)
    if (accountActiveFilter !== '') query.set('active', accountActiveFilter)
    const data = await api(`/api/finance/accounts?${query.toString()}`, {}, customToken)
    setAccountItems(data.items)
  }

  async function submitOrganization(event) {
    event.preventDefault()
    setError('')
    try {
      const payload = {
        code: orgForm.code.trim(),
        name: orgForm.name.trim(),
        type: orgForm.type.trim() || null,
        parentId: orgForm.parentId ? Number(orgForm.parentId) : null,
        active: orgForm.active,
      }
      if (!payload.code || !payload.name) throw new Error('조직 코드와 이름은 필수입니다.')

      if (orgEditId) {
        await api(`/api/organizations/${orgEditId}`, {
          method: 'PUT',
          body: JSON.stringify({
            name: payload.name,
            type: payload.type,
            parentId: payload.parentId,
            active: payload.active,
          }),
        })
      } else {
        await api('/api/organizations', { method: 'POST', body: JSON.stringify(payload) })
      }
      setOrgForm(emptyOrgForm)
      setOrgEditId(null)
      await loadOrganizations()
    } catch (err) {
      setError(err.message)
    }
  }

  async function submitCommonCode(event) {
    event.preventDefault()
    setError('')
    try {
      const payload = {
        groupCode: codeForm.groupCode.trim(),
        code: codeForm.code.trim(),
        name: codeForm.name.trim(),
        sortOrder: Number(codeForm.sortOrder || 0),
        description: codeForm.description.trim() || null,
        active: codeForm.active,
      }
      if (!payload.groupCode || !payload.code || !payload.name) {
        throw new Error('그룹코드/코드/코드명은 필수입니다.')
      }

      if (codeEditId) {
        await api(`/api/common-codes/${codeEditId}`, {
          method: 'PUT',
          body: JSON.stringify({
            name: payload.name,
            sortOrder: payload.sortOrder,
            description: payload.description,
            active: payload.active,
          }),
        })
      } else {
        await api('/api/common-codes', { method: 'POST', body: JSON.stringify(payload) })
      }
      setCodeForm(emptyCodeForm)
      setCodeEditId(null)
      await loadCommonCodes()
    } catch (err) {
      setError(err.message)
    }
  }

  async function submitFinancePeriod(event) {
    event.preventDefault()
    setError('')
    try {
      if (!periodForm.startDate || !periodForm.endDate) throw new Error('시작일/종료일은 필수입니다.')
      const payload = {
        fiscalYear: Number(periodForm.fiscalYear),
        periodNo: Number(periodForm.periodNo),
        startDate: periodForm.startDate,
        endDate: periodForm.endDate,
        status: periodForm.status,
        active: periodForm.active,
      }

      if (periodEditId) {
        await api(`/api/finance/periods/${periodEditId}`, {
          method: 'PUT',
          body: JSON.stringify({
            startDate: payload.startDate,
            endDate: payload.endDate,
            status: payload.status,
            active: payload.active,
          }),
        })
      } else {
        await api('/api/finance/periods', { method: 'POST', body: JSON.stringify(payload) })
      }
      setPeriodForm(emptyPeriodForm)
      setPeriodEditId(null)
      await loadFinancePeriods()
    } catch (err) {
      setError(err.message)
    }
  }

  async function submitFinanceAccount(event) {
    event.preventDefault()
    setError('')
    try {
      const payload = {
        accountCode: accountForm.accountCode.trim(),
        accountName: accountForm.accountName.trim(),
        accountType: accountForm.accountType,
        parentId: accountForm.parentId ? Number(accountForm.parentId) : null,
        active: accountForm.active,
      }
      if (!payload.accountCode || !payload.accountName || !payload.accountType) {
        throw new Error('계정코드/계정명/계정유형은 필수입니다.')
      }

      if (accountEditId) {
        await api(`/api/finance/accounts/${accountEditId}`, {
          method: 'PUT',
          body: JSON.stringify({
            accountName: payload.accountName,
            accountType: payload.accountType,
            parentId: payload.parentId,
            active: payload.active,
          }),
        })
      } else {
        await api('/api/finance/accounts', { method: 'POST', body: JSON.stringify(payload) })
      }
      setAccountForm(emptyAccountForm)
      setAccountEditId(null)
      await loadFinanceAccounts()
    } catch (err) {
      setError(err.message)
    }
  }

  async function removeOrganization(id) {
    try {
      await api(`/api/organizations/${id}`, { method: 'DELETE' })
      await loadOrganizations()
    } catch (err) {
      setError(err.message)
    }
  }

  async function removeCommonCode(id) {
    try {
      await api(`/api/common-codes/${id}`, { method: 'DELETE' })
      await loadCommonCodes()
    } catch (err) {
      setError(err.message)
    }
  }

  async function removeFinancePeriod(id) {
    try {
      await api(`/api/finance/periods/${id}`, { method: 'DELETE' })
      await loadFinancePeriods()
    } catch (err) {
      setError(err.message)
    }
  }

  async function removeFinanceAccount(id) {
    try {
      await api(`/api/finance/accounts/${id}`, { method: 'DELETE' })
      await loadFinanceAccounts()
    } catch (err) {
      setError(err.message)
    }
  }

  function selectOrg(item) {
    setOrgEditId(item.id)
    setOrgForm({
      code: item.code,
      name: item.name,
      type: item.type || '',
      parentId: item.parentId || '',
      active: item.active,
    })
  }

  function selectCode(item) {
    setCodeEditId(item.id)
    setCodeForm({
      groupCode: item.groupCode,
      code: item.code,
      name: item.name,
      sortOrder: item.sortOrder,
      description: item.description || '',
      active: item.active,
    })
  }

  function selectPeriod(item) {
    setPeriodEditId(item.id)
    setPeriodForm({
      fiscalYear: item.fiscalYear,
      periodNo: item.periodNo,
      startDate: item.startDate,
      endDate: item.endDate,
      status: item.status,
      active: item.active,
    })
  }

  function selectAccount(item) {
    setAccountEditId(item.id)
    setAccountForm({
      accountCode: item.accountCode,
      accountName: item.accountName,
      accountType: item.accountType,
      parentId: item.parentId || '',
      active: item.active,
    })
  }

  if (!loggedIn) {
    return (
      <main className="app-shell">
        <section className="card auth-card">
          <h1>Church ERP 로그인</h1>
          <form className="form" onSubmit={onLogin}>
            <label>로그인 ID</label>
            <input value={loginId} onChange={(e) => setLoginId(e.target.value)} required />
            <label>비밀번호</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            <button disabled={loading} type="submit">{loading ? '로그인 중...' : '로그인'}</button>
          </form>
          {error && <p className="error">{error}</p>}
        </section>
      </main>
    )
  }

  return (
    <main className="app-shell">
      <section className="card">
        <header className="top">
          <div>
            <h1>공통/재정 기본관리</h1>
            <p>{user.name} ({user.loginId})</p>
          </div>
          <button type="button" onClick={onLogout}>로그아웃</button>
        </header>

        <div className="tabs">
          <button type="button" className={tab === 'org' ? 'active' : ''} onClick={() => setTab('org')}>조직</button>
          <button type="button" className={tab === 'code' ? 'active' : ''} onClick={() => setTab('code')}>공통코드</button>
          <button type="button" className={tab === 'period' ? 'active' : ''} onClick={() => setTab('period')}>회계기간</button>
          <button type="button" className={tab === 'account' ? 'active' : ''} onClick={() => setTab('account')}>계정과목</button>
        </div>

        {tab === 'org' && (
          <section className="panel-grid">
            <article className="panel">
              <h2>조직 조회</h2>
              <div className="filter-row">
                <input placeholder="코드/이름" value={orgKeyword} onChange={(e) => setOrgKeyword(e.target.value)} />
                <select value={orgActive} onChange={(e) => setOrgActive(e.target.value)}>
                  <option value="">전체</option>
                  <option value="true">활성</option>
                  <option value="false">비활성</option>
                </select>
                <button type="button" onClick={() => void loadOrganizations()}>검색</button>
              </div>
              <div className="list">
                {orgItems.map((item) => (
                  <div key={item.id} className="row">
                    <button type="button" className="row-main" onClick={() => selectOrg(item)}>
                      <strong>{item.code}</strong> {item.name} ({item.active ? '활성' : '비활성'})
                    </button>
                    <button type="button" className="danger" onClick={() => void removeOrganization(item.id)}>삭제</button>
                  </div>
                ))}
              </div>
            </article>

            <article className="panel">
              <h2>{orgEditId ? '조직 수정' : '조직 등록'}</h2>
              <form className="form" onSubmit={submitOrganization}>
                <label>코드</label>
                <input value={orgForm.code} disabled={Boolean(orgEditId)} onChange={(e) => setOrgForm({ ...orgForm, code: e.target.value })} required />
                <label>이름</label>
                <input value={orgForm.name} onChange={(e) => setOrgForm({ ...orgForm, name: e.target.value })} required />
                <label>유형</label>
                <input value={orgForm.type} onChange={(e) => setOrgForm({ ...orgForm, type: e.target.value })} placeholder="DEPARTMENT" />
                <label>상위조직ID</label>
                <input value={orgForm.parentId} onChange={(e) => setOrgForm({ ...orgForm, parentId: e.target.value })} inputMode="numeric" />
                <label className="check"><input type="checkbox" checked={orgForm.active} onChange={(e) => setOrgForm({ ...orgForm, active: e.target.checked })} /> 활성</label>
                <button type="submit">저장</button>
              </form>
            </article>
          </section>
        )}

        {tab === 'code' && (
          <section className="panel-grid">
            <article className="panel">
              <h2>공통코드 조회</h2>
              <div className="filter-row">
                <input placeholder="groupCode" value={codeGroupFilter} onChange={(e) => setCodeGroupFilter(e.target.value)} />
                <input placeholder="코드/이름" value={codeKeyword} onChange={(e) => setCodeKeyword(e.target.value)} />
                <select value={codeActive} onChange={(e) => setCodeActive(e.target.value)}>
                  <option value="">전체</option>
                  <option value="true">활성</option>
                  <option value="false">비활성</option>
                </select>
                <button type="button" onClick={() => void loadCommonCodes()}>검색</button>
              </div>
              <div className="list">
                {codeItems.map((item) => (
                  <div key={item.id} className="row">
                    <button type="button" className="row-main" onClick={() => selectCode(item)}>
                      <strong>[{item.groupCode}] {item.code}</strong> {item.name} ({item.active ? '활성' : '비활성'})
                    </button>
                    <button type="button" className="danger" onClick={() => void removeCommonCode(item.id)}>삭제</button>
                  </div>
                ))}
              </div>
            </article>

            <article className="panel">
              <h2>{codeEditId ? '공통코드 수정' : '공통코드 등록'}</h2>
              <form className="form" onSubmit={submitCommonCode}>
                <label>그룹코드</label>
                <input value={codeForm.groupCode} disabled={Boolean(codeEditId)} onChange={(e) => setCodeForm({ ...codeForm, groupCode: e.target.value })} required />
                <label>코드</label>
                <input value={codeForm.code} disabled={Boolean(codeEditId)} onChange={(e) => setCodeForm({ ...codeForm, code: e.target.value })} required />
                <label>코드명</label>
                <input value={codeForm.name} onChange={(e) => setCodeForm({ ...codeForm, name: e.target.value })} required />
                <label>정렬순서</label>
                <input type="number" min="0" value={codeForm.sortOrder} onChange={(e) => setCodeForm({ ...codeForm, sortOrder: e.target.value })} />
                <label>설명</label>
                <input value={codeForm.description} onChange={(e) => setCodeForm({ ...codeForm, description: e.target.value })} />
                <label className="check"><input type="checkbox" checked={codeForm.active} onChange={(e) => setCodeForm({ ...codeForm, active: e.target.checked })} /> 활성</label>
                <button type="submit">저장</button>
              </form>
            </article>
          </section>
        )}

        {tab === 'period' && (
          <section className="panel-grid">
            <article className="panel">
              <h2>회계기간 조회</h2>
              <div className="filter-row">
                <input type="number" placeholder="회계연도" value={periodYearFilter} onChange={(e) => setPeriodYearFilter(e.target.value)} />
                <select value={periodStatusFilter} onChange={(e) => setPeriodStatusFilter(e.target.value)}>
                  <option value="">전체상태</option>
                  <option value="OPEN">OPEN</option>
                  <option value="CLOSED">CLOSED</option>
                </select>
                <select value={periodActiveFilter} onChange={(e) => setPeriodActiveFilter(e.target.value)}>
                  <option value="">전체</option>
                  <option value="true">활성</option>
                  <option value="false">비활성</option>
                </select>
                <button type="button" onClick={() => void loadFinancePeriods()}>검색</button>
              </div>
              <div className="list">
                {periodItems.map((item) => (
                  <div key={item.id} className="row">
                    <button type="button" className="row-main" onClick={() => selectPeriod(item)}>
                      <strong>{item.fiscalYear}-{item.periodNo}</strong> {item.startDate} ~ {item.endDate} ({item.status}, {item.active ? '활성' : '비활성'})
                    </button>
                    <button type="button" className="danger" onClick={() => void removeFinancePeriod(item.id)}>삭제</button>
                  </div>
                ))}
              </div>
            </article>

            <article className="panel">
              <h2>{periodEditId ? '회계기간 수정' : '회계기간 등록'}</h2>
              <form className="form" onSubmit={submitFinancePeriod}>
                <label>회계연도</label>
                <input type="number" value={periodForm.fiscalYear} disabled={Boolean(periodEditId)} onChange={(e) => setPeriodForm({ ...periodForm, fiscalYear: e.target.value })} required />
                <label>기간번호</label>
                <input type="number" min="1" value={periodForm.periodNo} disabled={Boolean(periodEditId)} onChange={(e) => setPeriodForm({ ...periodForm, periodNo: e.target.value })} required />
                <label>시작일</label>
                <input type="date" value={periodForm.startDate} onChange={(e) => setPeriodForm({ ...periodForm, startDate: e.target.value })} required />
                <label>종료일</label>
                <input type="date" value={periodForm.endDate} onChange={(e) => setPeriodForm({ ...periodForm, endDate: e.target.value })} required />
                <label>상태</label>
                <select value={periodForm.status} onChange={(e) => setPeriodForm({ ...periodForm, status: e.target.value })}>
                  <option value="OPEN">OPEN</option>
                  <option value="CLOSED">CLOSED</option>
                </select>
                <label className="check"><input type="checkbox" checked={periodForm.active} onChange={(e) => setPeriodForm({ ...periodForm, active: e.target.checked })} /> 활성</label>
                <button type="submit">저장</button>
              </form>
            </article>
          </section>
        )}

        {tab === 'account' && (
          <section className="panel-grid">
            <article className="panel">
              <h2>계정과목 조회</h2>
              <div className="filter-row">
                <input placeholder="코드/이름" value={accountKeyword} onChange={(e) => setAccountKeyword(e.target.value)} />
                <select value={accountTypeFilter} onChange={(e) => setAccountTypeFilter(e.target.value)}>
                  <option value="">전체유형</option>
                  <option value="ASSET">ASSET</option>
                  <option value="LIABILITY">LIABILITY</option>
                  <option value="EQUITY">EQUITY</option>
                  <option value="REVENUE">REVENUE</option>
                  <option value="EXPENSE">EXPENSE</option>
                </select>
                <select value={accountActiveFilter} onChange={(e) => setAccountActiveFilter(e.target.value)}>
                  <option value="">전체</option>
                  <option value="true">활성</option>
                  <option value="false">비활성</option>
                </select>
                <button type="button" onClick={() => void loadFinanceAccounts()}>검색</button>
              </div>
              <div className="list">
                {accountItems.map((item) => (
                  <div key={item.id} className="row">
                    <button type="button" className="row-main" onClick={() => selectAccount(item)}>
                      <strong>{item.accountCode}</strong> {item.accountName} ({item.accountType}, {item.active ? '활성' : '비활성'})
                    </button>
                    <button type="button" className="danger" onClick={() => void removeFinanceAccount(item.id)}>삭제</button>
                  </div>
                ))}
              </div>
            </article>

            <article className="panel">
              <h2>{accountEditId ? '계정과목 수정' : '계정과목 등록'}</h2>
              <form className="form" onSubmit={submitFinanceAccount}>
                <label>계정코드</label>
                <input value={accountForm.accountCode} disabled={Boolean(accountEditId)} onChange={(e) => setAccountForm({ ...accountForm, accountCode: e.target.value })} required />
                <label>계정명</label>
                <input value={accountForm.accountName} onChange={(e) => setAccountForm({ ...accountForm, accountName: e.target.value })} required />
                <label>계정유형</label>
                <select value={accountForm.accountType} onChange={(e) => setAccountForm({ ...accountForm, accountType: e.target.value })}>
                  <option value="ASSET">ASSET</option>
                  <option value="LIABILITY">LIABILITY</option>
                  <option value="EQUITY">EQUITY</option>
                  <option value="REVENUE">REVENUE</option>
                  <option value="EXPENSE">EXPENSE</option>
                </select>
                <label>상위계정ID</label>
                <input value={accountForm.parentId} onChange={(e) => setAccountForm({ ...accountForm, parentId: e.target.value })} inputMode="numeric" />
                <label className="check"><input type="checkbox" checked={accountForm.active} onChange={(e) => setAccountForm({ ...accountForm, active: e.target.checked })} /> 활성</label>
                <button type="submit">저장</button>
              </form>
            </article>
          </section>
        )}

        {error && <p className="error">{error}</p>}
      </section>
    </main>
  )
}

export default App
