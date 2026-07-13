import { Link } from 'react-router-dom'
import { useCfps } from '../hooks/useCfps.js'
import ConferenceCard from '../components/cfp/ConferenceCard.jsx'
import LoadingSpinner from '../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../components/ui/ErrorAlert.jsx'
import { isOpen } from '../utils/date.js'

function Stat({ value, label }) {
  return (
    <div>
      <div className="font-display font-extrabold text-[42px] leading-none text-white">{value}</div>
      <div className="mt-2 font-mono text-[12px] uppercase tracking-[.14em] text-muted-400">{label}</div>
    </div>
  )
}

export default function HomePage() {
  const { cfps, loading, error, reload } = useCfps()

  const openCount = cfps.filter(isOpen).length
  const trackCount = cfps.reduce((n, c) => n + (c.conferenceTracks?.length ?? 0), 0)
  const formatCount = cfps.reduce((n, c) => n + (c.conferenceSessionFormats?.length ?? 0), 0)

  return (
    <div className="-mx-8">
      {/* HERO */}
      <div className="relative overflow-hidden">
        <div
          className="absolute inset-0 pointer-events-none"
          style={{
            background:
              'radial-gradient(720px 560px at 82% 4%, rgba(70,149,235,.4), rgba(70,149,235,0) 62%), radial-gradient(560px 480px at 96% 42%, rgba(255,0,74,.16), transparent 60%), radial-gradient(560px 460px at -6% 96%, rgba(70,149,235,.16), transparent 60%)',
          }}
        />
        <div className="relative container-1180 pt-[100px] pb-[84px]">
          <img
            src="/quarkus-icon.svg"
            alt=""
            className="hidden lg:block absolute top-11 right-14 w-[220px] h-auto"
            style={{ filter: 'drop-shadow(0 26px 62px rgba(70,149,235,.45))' }}
          />
          <div className="relative z-10 max-w-[720px]">
            <div className="kicker mb-6 tracking-[.26em]">// Supersonic · Subatomic · Call for Papers</div>
            <h1 className="font-display font-black text-[64px] sm:text-[84px] leading-[.98] tracking-[-.03em] mb-6 text-white">
              The call for<br />papers is <span className="text-danger">open.</span>
            </h1>
            <p className="text-[19px] leading-[1.55] text-muted-200 max-w-[540px] mb-[38px]">
              Pitch a session on Quarkus, Java, and cloud-native to the developer community. Find your stage.
            </p>
            <div className="flex flex-wrap gap-3.5 items-center">
              <Link to="/submit" className="btn-primary !h-[54px] !px-[30px] !text-[17px] !font-bold shadow-btn-lg">
                Submit your talk →
              </Link>
              <a href="#conferences" className="btn-ghost !h-[54px] !px-7 !text-[17px]">
                Browse conferences
              </a>
            </div>
          </div>

          <div className="relative z-10 flex gap-14 mt-[74px] pt-9 border-t border-white/10">
            <Stat value={openCount} label="Open calls" />
            <Stat value={trackCount} label="Tracks" />
            <Stat value={formatCount} label="Session formats" />
          </div>
        </div>
      </div>

      {/* CONFERENCES */}
      <div id="conferences" className="bg-ink-2 border-t border-white/[.06]">
        <div className="container-1180 pt-16 pb-[88px]">
          <div className="flex items-end justify-between mb-7">
            <div>
              <div className="kicker mb-2.5">Conferences</div>
              <h2 className="font-display font-extrabold text-[34px] tracking-[-.02em] text-white">
                Find your call for papers
              </h2>
            </div>
            <Link to="/reviews" className="font-mono text-[12.5px] uppercase tracking-[.1em] text-brand-light">
              Review queue →
            </Link>
          </div>

          {loading && <LoadingSpinner />}
          {error && <ErrorAlert message={error} onDismiss={reload} />}
          {!loading && !error && cfps.length === 0 && (
            <p className="text-muted-400 text-center py-16">No CFPs yet.</p>
          )}

          <div className="grid gap-[22px] sm:grid-cols-2">
            {cfps.map(cfp => <ConferenceCard key={cfp.id} cfp={cfp} />)}
          </div>
        </div>
      </div>
    </div>
  )
}
