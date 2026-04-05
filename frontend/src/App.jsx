import { useEffect, useMemo, useState } from 'react'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const TOKEN_KEY = 'church_erp_access_token'

function App() {
  const [loginId, setLoginId] = useState('admin')
  const [password, setPassword] = useState('password')
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY) || '')
  const [user, setUser] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const loggedIn = useMemo(() => token.length > 0 && user !== null, [token, user])

  useEffect(() => {
    if (!token) {
      setUser(null)
      return
    }

    fetchMe(token)
  }, [token])

  async function fetchMe(accessToken) {
    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
        headers: { Authorization: `Bearer ${accessToken}` },
      })
      const json = await response.json()
      if (!response.ok || !json.success) {
        throw new Error(json.message || '인증 확인에 실패했습니다.')
      }
      setUser(json.data)
    } catch (err) {
      setToken('')
      localStorage.removeItem(TOKEN_KEY)
      setUser(null)
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function onSubmit(event) {
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
      if (!response.ok || !json.success) {
        throw new Error(json.message || '로그인에 실패했습니다.')
      }

      const accessToken = json.data.accessToken
      localStorage.setItem(TOKEN_KEY, accessToken)
      setToken(accessToken)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function logout() {
    if (!token) return

    try {
      await fetch(`${API_BASE_URL}/api/auth/logout`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
      })
    } finally {
      localStorage.removeItem(TOKEN_KEY)
      setToken('')
      setUser(null)
    }
  }

  return (
    <main className="app-shell">
      <section className="card">
        <header>
          <h1>Church ERP 인증 부트스트랩</h1>
          <p>Spring Boot + React + MariaDB</p>
        </header>

        {!loggedIn && (
          <form className="form" onSubmit={onSubmit}>
            <label htmlFor="loginId">로그인 ID</label>
            <input id="loginId" value={loginId} onChange={(e) => setLoginId(e.target.value)} required />

            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <button disabled={loading} type="submit">
              {loading ? '로그인 중...' : '로그인'}
            </button>
          </form>
        )}

        {loggedIn && (
          <section className="panel">
            <h2>현재 사용자</h2>
            <p>이름: {user.name}</p>
            <p>로그인 ID: {user.loginId}</p>
            <p>역할: {user.roles.join(', ')}</p>
            <p>권한: {user.permissions.join(', ') || '-'}</p>
            <button onClick={logout} type="button">
              로그아웃
            </button>
          </section>
        )}

        {error && <p className="error">{error}</p>}
      </section>
    </main>
  )
}

export default App
