export function encodeObj(obj) {
  let target = { ...obj }
  for (let a in target) {
    if (typeof target[a] === 'string') {
      target[a] = encodeURIComponent(target[a])
    }
  }
  return target
}

export function decodeObj(obj) {
  for (let a in obj) {
    if (typeof obj[a] === 'string') {
      obj[a] = decodeURIComponent(obj[a])
    }
  }
  return obj
}
