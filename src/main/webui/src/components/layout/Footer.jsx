export default function Footer() {
  return (
    <footer className="border-t border-white/[.06] bg-ink">
      <div className="container-1180 flex items-center justify-between py-8">
        <div className="flex items-center gap-3">
          <img src="/quarkus-icon.svg" alt="" className="w-6 h-6" />
          <span className="font-mono text-[12px] text-muted-500">
            Quarkus CFP · Supersonic Subatomic Java
          </span>
        </div>
        <span className="font-mono text-[12px] text-muted-500">cfp@quarkus.io</span>
      </div>
    </footer>
  )
}
