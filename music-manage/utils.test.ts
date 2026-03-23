import { getBirth, parseLyric } from './src/utils'

describe('utils', () => {
  describe('getBirth', () => {
    it('should return empty string for null', () => {
      expect(getBirth(null)).toBe('')
    })

    it('should return empty string for empty string', () => {
      expect(getBirth('')).toBe('')
    })

    it('should format date correctly', () => {
      const result = getBirth('1995-03-15T00:00:00.000Z')
      expect(result).toMatch(/^\d{4}-\d{2}-\d{2}$/)
    })
  })

  describe('parseLyric', () => {
    it('should return array with text for invalid lyric format', () => {
      const result = parseLyric('hello world')
      expect(result).toEqual(['hello world'])
    })

    it('should parse valid lyric text', () => {
      const lyric = '[00:01.00]第一行歌词\n[00:02.50]第二行歌词'
      const result = parseLyric(lyric)
      expect(result).toEqual(['第一行歌词', '第二行歌词'])
    })
  })
})