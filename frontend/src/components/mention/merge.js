import mergeDeep from 'components/mention/mergeDeep'

export const merge = (target, ...sources) => {
  return sources.reduce((t, s) => {
    return mergeDeep(t, s)
  }, target)
}

