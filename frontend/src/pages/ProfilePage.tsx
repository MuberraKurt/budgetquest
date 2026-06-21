import { useEffect, useState, type FormEvent } from 'react';
import { getProfile, updateProfile } from '../api/budgetquest';
import type { UserProfile } from '../api/types';

export function ProfilePage() {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [displayName, setDisplayName] = useState('');
  const [currency, setCurrency] = useState('USD');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getProfile()
      .then((data) => {
        setProfile(data);
        setDisplayName(data.displayName);
        setCurrency(data.currency);
      })
      .catch(() => setError('Could not load profile.'))
      .finally(() => setLoading(false));
  }, []);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSaving(true);
    setMessage(null);
    setError(null);

    try {
      const updated = await updateProfile({ displayName, currency: currency.toUpperCase() });
      setProfile(updated);
      setMessage('Profile updated.');
    } catch {
      setError('Could not update profile.');
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <div className="loading-screen inline">
        <div className="spinner" />
      </div>
    );
  }

  return (
    <div className="page-stack">
      <section className="page-hero">
        <div>
          <p className="eyebrow">Account</p>
          <h1>Profile</h1>
          <p className="subtitle">Manage how your name and preferred currency appear in the app.</p>
        </div>
      </section>

      {error ? <div className="alert">{error}</div> : null}
      {message ? <div className="success-banner">{message}</div> : null}

      <form className="panel form-panel narrow" onSubmit={handleSubmit}>
        <div className="panel-header">
          <h2>Preferences</h2>
          <span>{profile?.keycloakUserId}</span>
        </div>

        <div className="form-grid">
          <label>
            Display name
            <input
              required
              maxLength={80}
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
            />
          </label>
          <label>
            Currency
            <input
              required
              minLength={3}
              maxLength={3}
              value={currency}
              onChange={(e) => setCurrency(e.target.value.toUpperCase())}
              placeholder="USD"
            />
          </label>
        </div>

        <button type="submit" className="primary-button" disabled={saving}>
          {saving ? 'Saving...' : 'Save profile'}
        </button>
      </form>
    </div>
  );
}
