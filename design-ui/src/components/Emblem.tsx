export function Emblem({ size = 48, className = '' }: { size?: number; className?: string }) {
  return (
    <img
      src="./emblem.png"
      alt="市场监管徽章"
      width={size}
      height={size}
      draggable={false}
      className={`select-none object-contain ${className}`}
    />
  )
}
